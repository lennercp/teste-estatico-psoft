package com.ufcg.psoft.commerce.service.chamado;

import com.ufcg.psoft.commerce.dto.*;

import com.ufcg.psoft.commerce.exception.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.atribuicao.AtribuicaoService;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChamadoServiceImpl implements ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ServicoRepository servicoRepository;
    private final AtribuicaoService atribuicaoService;

    private void autorizarAcessoChamado(AuthRequestDTO auth) {

        switch (auth.getTipo()) {
            case CLIENTE -> authService.autenticarCliente(auth.getClienteId(), auth.getCodigoAcesso());
            case EMPRESA -> authService.autenticarEmpresa(auth.getEmpresaCnpj(), auth.getCodigoAcesso());
            case ADMIN -> throw new AcessoNegadoException();
            default -> throw new IllegalArgumentException("Tipo de usuário inválido: " + auth.getTipo());
        }
    }

    @Override
    @Transactional
    public ChamadoResponseDTO criar(ChamadoPostPutRequestDTO dto, AuthRequestDTO auth) {
        if (!auth.getTipo().equals(TipoUsuario.CLIENTE)) {
            throw new AcessoNegadoException();
        }
        authService.autenticar(auth);

        Empresa empresa = empresaRepository.findByCnpj(dto.getEmpresaCnpj())
                .orElseThrow(EmpresaNaoExisteException::new);
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(ClienteNaoExisteException::new);
        Servico servico = servicoRepository.findById(dto.getServicoId())
                .orElseThrow(ServicoNaoExisteException::new);

        if (servico.getTipoPlano().equals(TipoPlano.PREMIUM) && !cliente.getPlanoAtual().equals(TipoPlano.PREMIUM)) {
            throw new TipoPlanoIncorretoException();
        }

        if (Boolean.FALSE.equals(servico.getAtivo())) {
            throw new ServicoDisponivelException();
        }

        Chamado chamado = Chamado.builder()
                .cliente(cliente)
                .servico(servico)
                .empresa(empresa)
                .endereco(dto.getEndereco() == null || dto.getEndereco().isBlank() ? cliente.getEndereco()
                        : dto.getEndereco())
                .status(StatusChamado.RECEBIDO) // Estado inicial
                .build();

        return modelMapper.map(chamadoRepository.save(chamado), ChamadoResponseDTO.class);
    }

    private void avancarStatus(Chamado chamado) {

        if (chamado.getStatus() == StatusChamado.AGUARDANDO_TECNICO) {
            throw new ChamadoBloqueadoParaAvancarStatus();
        }

        chamado.avancarStatus();

        if (chamado.getStatus() == StatusChamado.AGUARDANDO_TECNICO) {
            atribuicaoService.processarChamadoEmAndamento(chamado);
        }

    }

    private void cancelarStatus(Chamado chamado, Long clienteSolicitanteId) {
        StatusChamado statusCancelado = (StatusChamado) chamado.getStatus().cancelar(
                clienteSolicitanteId,
                chamado.getCliente().getId());
        chamado.setStatus(statusCancelado);
    }

    @Override
    @Transactional
    public ChamadoResponseDTO atualizar(long id, ChamadoPatchRequestDTO dto, AuthRequestDTO auth) {
        Chamado chamado = chamadoRepository.findById(id).orElseThrow(ChamadoNaoExisteException::new);
        authService.autenticar(auth);
        autorizarAcessoChamado(auth);

        if (dto.getEndereco() != null) {
            chamado.setEndereco(dto.getEndereco());
        }

        // Lógica de Transição de Estado (State Pattern)
        if (dto.getStatusAcao() != null) {
            String acao = dto.getStatusAcao().toUpperCase();
            if ("AVANCAR".equals(acao)) {
                this.avancarStatus(chamado);
            } else if ("CANCELAR".equals(acao)) {
                this.cancelarStatus(chamado, auth.getClienteId());
            }
        }

        return modelMapper.map(chamadoRepository.save(chamado), ChamadoResponseDTO.class);
    }

    @Override
    public ChamadoResponseDTO recuperar(Long id, AuthRequestDTO auth) {

        Chamado chamado = chamadoRepository.findById(id).orElseThrow(ChamadoNaoExisteException::new);

        authService.autenticar(auth);

        autorizarAcessoChamado(auth);

        return modelMapper.map(chamado, ChamadoResponseDTO.class);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deletar(Long id, AuthRequestDTO auth) {

        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(ChamadoNaoExisteException::new);
        authService.autenticar(auth);
        autorizarAcessoChamado(auth);
        this.cancelarStatus(chamado, auth.getClienteId());
        chamadoRepository.delete(chamado);

        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public void confirmarPagamento(
            Long chamadoId,
            ChamadoPagamentoRequestDTO dto,
            AuthRequestDTO auth) {

        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(ChamadoNaoExisteException::new);

        authService.autenticar(auth);

        if (auth.getTipo() != TipoUsuario.CLIENTE) {
            throw new AcessoNegadoException();
        }

        autorizarAcessoChamado(auth);
        if (chamado.getPagamento() != null) {
            throw new ChamadoJaPossuiPagamentoConfirmadoException();
        }

        Pagamento pagamento = Pagamento.builder()
                .chamado(chamado)
                .metodo(dto.getMetodo())
                .confirmadoEm(LocalDateTime.now())
                .build();
        chamado.setPagamento(pagamento);

        pagamentoRepository.save(pagamento);
    }

}
