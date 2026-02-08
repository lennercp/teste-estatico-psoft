package com.ufcg.psoft.commerce.service.chamado;

import com.ufcg.psoft.commerce.dto.*;

import com.ufcg.psoft.commerce.exception.AcessoNegadoException;
import com.ufcg.psoft.commerce.exception.ChamadoNaoExisteException;
import com.ufcg.psoft.commerce.exception.TipoPlanoIncorretoException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChamadoServiceImpl implements ChamadoService{

    private final ChamadoRepository chamadoRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ServicoRepository servicoRepository;

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
            case ADMIN -> {
                throw new AcessoNegadoException();
            }
        }
    }

    private void atualizarCampos(Chamado chamado, ChamadoPatchRequestDTO dto) {
        if (dto.getEndereco() != null)
            chamado.setEndereco(dto.getEndereco());
    }

    @Override
    public ChamadoResponseDTO criar(ChamadoPostPutRequestDTO dto, AuthRequestDTO auth) {

        if (!auth.getTipo().equals(TipoUsuario.CLIENTE)) {
            throw new AcessoNegadoException();
        }

        Empresa empresa = empresaRepository.findByCnpj(dto.getEmpresaCnpj())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        Cliente cliente = clienteRepository.findById(dto.getCliente_id())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Servico servico = servicoRepository.findById(dto.getServico_id())
                .orElseThrow(() -> new EntityNotFoundException("Servico não encontrado"));

        authService.autenticar(auth);

        if(servico.getTipoPlano().equals(TipoPlano.PREMIUM) && !cliente.getPlanoAtual().equals(TipoPlano.PREMIUM)){
            throw new TipoPlanoIncorretoException();
        }

        Chamado chamado = new Chamado();

        chamado.setCliente(cliente);
        chamado.setServico(servico);
        chamado.setEmpresa(empresa);

        if (dto.getEndereco() == null || dto.getEndereco().isBlank()) {
            chamado.setEndereco(cliente.getEndereco());
        } else {
            chamado.setEndereco(dto.getEndereco());
        }

        chamadoRepository.save(chamado);

        return modelMapper.map(chamado, ChamadoResponseDTO.class);
    }

    @Override
    public ChamadoResponseDTO recuperar(Long id, AuthRequestDTO auth) {

        Chamado chamado = chamadoRepository.findById(id).orElseThrow(ChamadoNaoExisteException::new);

        authService.autenticar(auth);

        autorizarAcessoChamado(chamado, auth);

        return modelMapper.map(chamado, ChamadoResponseDTO.class);
    }

    @Override
    public ChamadoResponseDTO atualizar(long id, ChamadoPatchRequestDTO dto, AuthRequestDTO auth){

        Chamado chamado = chamadoRepository.findById(id).orElseThrow(ChamadoNaoExisteException::new);

        authService.autenticar(auth);

        autorizarAcessoChamado(chamado, auth);

        atualizarCampos(chamado, dto);

        chamadoRepository.save(chamado);

        return modelMapper.map(chamado, ChamadoResponseDTO.class);
    }

    @Override
    public ResponseEntity<Void> deletar(Long id, AuthRequestDTO auth) {
        Chamado chamado = chamadoRepository.findById(id).orElseThrow(ChamadoNaoExisteException::new);

        authService.autenticar(auth);

        autorizarAcessoChamado(chamado, auth);

        chamadoRepository.delete(chamado);
        return ResponseEntity.noContent().build();
    }

    @Override
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
            throw new IllegalStateException("Chamado já possui pagamento confirmado");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setChamado(chamado);
        pagamento.setMetodo(dto.getMetodo());
        pagamento.setConfirmadoEm(LocalDateTime.now());

        pagamentoRepository.save(pagamento);
    }

}
