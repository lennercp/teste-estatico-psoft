package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;
import com.ufcg.psoft.commerce.exception.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
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
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceImplTest {

    @InjectMocks
    private ChamadoServiceImpl service;

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ChamadoRepository chamadoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private TecnicoRepository tecnicoRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ModelMapper modelMapper;

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
                .ativo(true)
                .build();

        chamado = Chamado.builder()
                .id(100L)
                .cliente(cliente)
                .empresa(empresa)
                .servico(servico)
                .status(StatusChamado.RECEBIDO)
                .endereco("Rua A")
                .build();
    }

    @Test
    void criarSucesso() {
        ChamadoPostPutRequestDTO dto = ChamadoPostPutRequestDTO.builder()
                .clienteId(1L)
                .empresaCnpj("111")
                .servicoId(10L)
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
                .clienteId(1L).empresaCnpj("111").servicoId(10L).build();

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

        assertThrows(ChamadoJaPossuiPagamentoConfirmadoException.class,
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

    // --- NOVOS TESTES: STATE PATTERN E TRANSIÇÕES ---

    @Test
    void atualizarAvancarParaAnaliseSucesso() {
        AuthRequestDTO auth = AuthRequestDTO.empresa("111", "123");
        chamado.setStatus(StatusChamado.RECEBIDO);

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        ChamadoPatchRequestDTO dto = ChamadoPatchRequestDTO.builder()
                .statusAcao("AVANCAR")
                .build();

        service.atualizar(100L, dto, auth);

        assertEquals(StatusChamado.EM_ANALISE, chamado.getStatus());
        verify(chamadoRepository).save(chamado);
    }

    @Test
    void atualizarAvancarParaAtendimentoSucessoComTecnico() {
        AuthRequestDTO auth = AuthRequestDTO.empresa("111", "123");
        chamado.setStatus(StatusChamado.AGUARDANDO_TECNICO);
        Tecnico tecnico = Tecnico.builder().id(50L).build();

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));
        when(tecnicoRepository.findById(50L)).thenReturn(Optional.of(tecnico));

        ChamadoPatchRequestDTO dto = ChamadoPatchRequestDTO.builder()
                .statusAcao("AVANCAR")
                .tecnicoId(50L)
                .build();

        service.atualizar(100L, dto, auth);

        assertEquals(StatusChamado.ATENDIMENTO, chamado.getStatus());
        assertEquals(tecnico, chamado.getTecnico());
    }

    @Test
    void atualizarAvancarParaAtendimentoErroSemTecnico() {
        AuthRequestDTO auth = AuthRequestDTO.empresa("111", "123");
        chamado.setStatus(StatusChamado.AGUARDANDO_TECNICO);

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        ChamadoPatchRequestDTO dto = ChamadoPatchRequestDTO.builder()
                .statusAcao("AVANCAR")
                .tecnicoId(null)
                .build();

        assertThrows(TecnicoNaoInformadoException.class, () -> service.atualizar(100L, dto, auth));
    }

    // CANCELAMENTO (DELETAR)

    @Test
    void deletarSucessoQuandoAindaNaoAtendimento() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");
        chamado.setStatus(StatusChamado.RECEBIDO);

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        service.deletar(100L, auth);

        verify(chamadoRepository).delete(chamado);
    }

    @Test
    void deletarErroQuandoEmAtendimento() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");
        chamado.setStatus(StatusChamado.ATENDIMENTO); // Estado que PROIBE cancelar

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));
        assertThrows(ClienteNaoAutorizadoCancelarChamadoException.class,
                () -> service.deletar(100L, auth));
    }

    @Test
    void deletarErroClienteNaoDono() {
        AuthRequestDTO auth = AuthRequestDTO.cliente(999L, "123"); // Outro cliente
        chamado.setStatus(StatusChamado.RECEBIDO);

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        assertThrows(AcessoNegadoException.class, () -> service.deletar(100L, auth));
    }

    @Test
    void cancelarErroQuandoEmAtendimento() {

        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");
        chamado.setStatus(StatusChamado.ATENDIMENTO); // Status que PROIBE cancelamento

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        assertThrows(ClienteNaoAutorizadoCancelarChamadoException.class, () -> {
            service.deletar(100L, auth);
        });

        verify(chamadoRepository, never()).delete(any());
    }

    @Test
    void cancelarErroClienteDiferente() {

        Long outroClienteId = 999L;
        AuthRequestDTO auth = AuthRequestDTO.cliente(outroClienteId, "123");

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));
        assertThrows(AcessoNegadoException.class, () -> {
            service.deletar(100L, auth);
        });

        verify(chamadoRepository, never()).delete(any());
    }

    @Test
    void cancelarErroQuandoConcluido() {

        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");
        chamado.setStatus(StatusChamado.CONCLUIDO);

        when(chamadoRepository.findById(100L)).thenReturn(Optional.of(chamado));

        assertThrows(ChamadoConcluidoNaoCanceladoException.class, () -> {
            service.deletar(100L, auth);
        });
    }

    @Test
    void devePublicarEventoQuandoEntrarEmAtendimento() {

        chamado.setStatus(StatusChamado.AGUARDANDO_TECNICO);

        ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
        dto.setStatusAcao("AVANCAR");
        dto.setTecnicoId(1L);

        AuthRequestDTO auth = AuthRequestDTO.cliente(1L, "123");

        when(chamadoRepository.findById(100L))
                .thenReturn(Optional.of(chamado));

        when(tecnicoRepository.findById(1L))
                .thenReturn(Optional.of(new Tecnico()));

        when(chamadoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(), eq(ChamadoResponseDTO.class)))
                .thenReturn(new ChamadoResponseDTO());

        doNothing().when(authService).autenticar(any());

        service.atualizar(100L, dto, auth);

        verify(eventPublisher)
                .publishEvent(any(ChamadoEmAtendimentoEvent.class));
    }
}
