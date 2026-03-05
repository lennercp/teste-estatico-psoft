package com.ufcg.psoft.commerce.service.chamado;

import com.ufcg.psoft.commerce.dto.*;

import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;
import com.ufcg.psoft.commerce.exception.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    private void autorizarAcessoChamado(Chamado chamado, AuthRequestDTO auth) {

        switch (auth.getTipo()) {
            case CLIENTE -> {
                if (!chamado.getCliente().getId().equals(auth.getClienteId())) {
                    throw new AcessoNegadoException();
                }
            }
            case EMPRESA -> {
                if (!chamado.getEmpresa().getCnpj().equals(auth.getEmpresaCnpj())) {
                    throw new AcessoNegadoException();
                }
            }
            case ADMIN ->
                throw new AcessoNegadoException();
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

        if (Boolean.FALSE.equals(servico.getAtivo()))
            throw new ServicoDisponivelException();

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

        if (chamado.getStatus() == StatusChamado.ATENDIMENTO) {
            eventPublisher.publishEvent(
                    new ChamadoEmAtendimentoEvent(chamado));
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
        autorizarAcessoChamado(chamado, auth);

        if (dto.getEndereco() != null)
            chamado.setEndereco(dto.getEndereco());

        // Lógica de Transição de Estado (State Pattern)
        if (dto.getStatusAcao() != null) {
            String acao = dto.getStatusAcao().toUpperCase();
            if (acao.equals("AVANCAR")) {
                this.avancarStatus(chamado);
            } else if (acao.equals("CANCELAR")) {
                this.cancelarStatus(chamado, auth.getClienteId());
            }
        }

        return modelMapper.map(chamadoRepository.save(chamado), ChamadoResponseDTO.class);
    }

    @Override
    public ChamadoResponseDTO recuperar(Long id, AuthRequestDTO auth) {

        Chamado chamado = chamadoRepository.findById(id).orElseThrow(ChamadoNaoExisteException::new);

        authService.autenticar(auth);

        autorizarAcessoChamado(chamado, auth);

        return modelMapper.map(chamado, ChamadoResponseDTO.class);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deletar(Long id, AuthRequestDTO auth) {

        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(ChamadoNaoExisteException::new);
        authService.autenticar(auth);
        autorizarAcessoChamado(chamado, auth);
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

        autorizarAcessoChamado(chamado, auth);
        if (chamado.getPagamento() != null) {
            throw new ChamadoJaPossuiPagamentoConfirmadoException();
        }

        Pagamento pagamento = Pagamento.builder()
                .chamado(chamado)
                .metodo(dto.getMetodo())
                .confirmadoEm(LocalDateTime.now())
                .build();
        chamado.setPagamento((pagamento));

        pagamentoRepository.save(pagamento);
    }

}
