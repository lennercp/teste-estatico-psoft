package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.exception.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.auth.AuthService;

import com.ufcg.psoft.commerce.service.chamado.ChamadoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceImplTest {

    @InjectMocks
    private ChamadoServiceImpl service;

    @Mock private ChamadoRepository chamadoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private AuthService authService;
    @Mock private ModelMapper modelMapper;

    private Cliente cliente;
    private Empresa empresa;
    private Servico servico;
    private Chamado chamado;

    @BeforeEach
    void setup() {
        cliente = Cliente.builder()
                .id(1L)
                .endereco("Rua A")
                .planoAtual(TipoPlano.BASICO)
                .build();

        empresa = Empresa.builder()
                .cnpj("111")
                .build();

        servico = Servico.builder()
                .id(10L)
                .tipoPlano(TipoPlano.BASICO)
                .empresa(empresa)
                .build();

        chamado = Chamado.builder()
                .id(100L)
                .cliente(cliente)
                .empresa(empresa)
                .servico(servico)
                .endereco("Rua A")
                .build();
    }

    @Test
    void criarSucesso() {
        ChamadoPostPutRequestDTO dto = ChamadoPostPutRequestDTO.builder()
                .cliente_id(1L)
                .empresaCnpj("111")
                .servico_id(10L)
                .endereco("X")
                .build();

        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(empresaRepository.findByCnpj("111")).thenReturn(Optional.of(empresa));
        when(servicoRepository.findById(10L)).thenReturn(Optional.of(servico));
        when(modelMapper.map(any(), eq(ChamadoResponseDTO.class)))
                .thenReturn(new ChamadoResponseDTO());

        ChamadoResponseDTO resp = service.criar(dto, auth);

        assertNotNull(resp);
        verify(chamadoRepository).save(any());
    }

    @Test
    void criarNaoCliente() {
        AuthRequestDTO auth = AuthRequestDTO.admin("123");

        assertThrows(AcessoNegadoException.class,
                () -> service.criar(new ChamadoPostPutRequestDTO(), auth));
    }

    @Test
    void criarPlanoIncorreto() {
        servico.setTipoPlano(TipoPlano.PREMIUM);

        ChamadoPostPutRequestDTO dto = ChamadoPostPutRequestDTO.builder()
                .cliente_id(1L).empresaCnpj("111").servico_id(10L).build();

        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(empresaRepository.findByCnpj("111")).thenReturn(Optional.of(empresa));
        when(servicoRepository.findById(10L)).thenReturn(Optional.of(servico));

        assertThrows(TipoPlanoIncorretoException.class,
                () -> service.criar(dto, auth));
    }

    @Test
    void recuperarSucesso() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));
        when(modelMapper.map(any(), eq(ChamadoResponseDTO.class)))
                .thenReturn(new ChamadoResponseDTO());

        ChamadoResponseDTO resp = service.recuperar(100L, auth);

        assertNotNull(resp);
    }

    @Test
    void recuperarSemPermissao() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(999L, "123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        assertThrows(AcessoNegadoException.class,
                () -> service.recuperar(100L, auth));
    }

    @Test
    void atualizarSucesso() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));
        when(modelMapper.map(any(), eq(ChamadoResponseDTO.class)))
                .thenReturn(new ChamadoResponseDTO());

        ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
        dto.setEndereco("Novo");

        ChamadoResponseDTO resp = service.atualizar(100L, dto, auth);

        assertEquals("Novo", chamado.getEndereco());
        assertNotNull(resp);
    }

    @Test
    void deletarSucesso() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        service.deletar(100L, auth);

        verify(chamadoRepository).delete(chamado);
    }

    @Test
    void confirmarPagamentoSucesso() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        ChamadoPagamentoRequestDTO dto = new ChamadoPagamentoRequestDTO();
        dto.setMetodo(MeioPagamento.PIX);

        service.confirmarPagamento(100L, dto, auth);

        verify(pagamentoRepository).save(any());
    }

    @Test
    void confirmarPagamentoJaPago() {
        chamado.setPagamento(new Pagamento());

        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");
        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        ChamadoPagamentoRequestDTO dto = new ChamadoPagamentoRequestDTO();
        dto.setMetodo(MeioPagamento.PIX);

        assertThrows(IllegalStateException.class,
                () -> service.confirmarPagamento(100L, dto, auth));
    }

    @Test
    void confirmarPagamentoNaoCliente() {
        AuthRequestDTO auth = AuthRequestDTO.admin("123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        ChamadoPagamentoRequestDTO dto = new ChamadoPagamentoRequestDTO();
        dto.setMetodo(MeioPagamento.PIX);

        assertThrows(AcessoNegadoException.class,
                () -> service.confirmarPagamento(100L, dto, auth));
    }
}
