package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.service.atribuicao.AtribuicaoService;
import com.ufcg.psoft.commerce.service.auth.AuthService;

import com.ufcg.psoft.commerce.service.tecnico.TecnicoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Técnicos")
class TecnicoServiceTests {

    @InjectMocks
    private TecnicoServiceImpl tecnicoService;

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AtribuicaoService atribuicaoService;

    private Tecnico tecnico;
    private TecnicoPostPutRequestDTO tecnicoDTO;
    private TecnicoResponseDTO tecnicoResponseDTO;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        tecnico = Tecnico.builder()
                .id(1L)
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .disponibilidade(DisponibilidadeStatus.DESCANSO)
                .disponibilidadeAtualizadaEm(LocalDateTime.now().minusHours(1))
                .build();

        empresa = Empresa.builder()
                .cnpj("12.345.678/0001-99")
                .nomeFantasia("Empresa Teste")
                .endereco("Rua Teste, 1")
                .codigoAcesso("111111")
                .build();

        tecnicoDTO = TecnicoPostPutRequestDTO.builder()
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .build();

        tecnicoResponseDTO = TecnicoResponseDTO.builder()
                .id(1L)
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .build();
    }

    @Test
    @DisplayName("Deve criar um técnico com sucesso")
    void testCriarTecnico() {
        when(modelMapper.map(tecnicoDTO, Tecnico.class)).thenReturn(tecnico);
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.criar(tecnicoDTO);

        assertNotNull(resultado);
        assertEquals(tecnicoResponseDTO.getId(), resultado.getId());
        verify(tecnicoRepository, times(1)).save(tecnico);
    }

    @Test
    @DisplayName("Deve listar técnicos com sucesso")
    void testListarTecnicos() {
        when(tecnicoRepository.findAll()).thenReturn(List.of(tecnico));
        when(modelMapper.map(any(Tecnico.class), eq(TecnicoResponseDTO.class))).thenReturn(tecnicoResponseDTO);

        List<TecnicoResponseDTO> resultado = tecnicoService.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve recuperar técnico por ID com sucesso")
    void testRecuperarTecnicoPorId() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.recuperar(1L);

        assertNotNull(resultado);
        assertEquals(tecnico.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve atualizar técnico quando o código de acesso está correto")
    void testAtualizarTecnicoSucesso() {
        doNothing().when(authService).autenticarTecnico(eq(1L), eq("123456"));

        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        doNothing().when(modelMapper).map(tecnicoDTO, tecnico);

        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.atualizar(1L, "123456", tecnicoDTO);

        assertNotNull(resultado);
        verify(authService, times(1)).autenticarTecnico(eq(1L), eq("123456"));
        verify(tecnicoRepository, times(1)).save(tecnico);

        verify(modelMapper, times(1)).map(tecnicoDTO, tecnico);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar técnico com código de acesso incorreto")
    void testAtualizarTecnicoCodigoInvalido() {

        doThrow(new CodigoDeAcessoInvalidoException())
                .when(authService).autenticarTecnico(eq(1L), eq("000000"));

        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            tecnicoService.atualizar(1L, "000000", tecnicoDTO);
        });

        verify(tecnicoRepository, never()).findById(any());
        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover técnico com sucesso")
    void testRemoverTecnicoSucesso() {
        doNothing().when(authService).autenticarTecnico(1L, "123456");

        tecnicoService.remover(1L, "123456");

        verify(authService, times(1)).autenticarTecnico(1L, "123456");
        verify(tecnicoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve falhar ao remover técnico com código de acesso incorreto")
    void testRemoverTecnicoCodigoInvalido() {
        doThrow(new CodigoDeAcessoInvalidoException())
                .when(authService).autenticarTecnico(eq(1L), eq("999999"));

        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            tecnicoService.remover(1L, "999999");
        });
        verify(tecnicoRepository, never()).deleteById(any());
        verify(tecnicoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Técnico deve iniciar indisponível (DESCANSO)")
    void testTecnicoIniciaDescanso() {

        when(modelMapper.map(tecnicoDTO, Tecnico.class))
                .thenReturn(tecnico);

        when(tecnicoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(Tecnico.class),
                eq(TecnicoResponseDTO.class)))
                .thenReturn(tecnicoResponseDTO);

        tecnicoService.criar(tecnicoDTO);

        assertEquals(
                DisponibilidadeStatus.DESCANSO,
                tecnico.getDisponibilidade());
    }

    @Test
    @DisplayName("Deve alterar disponibilidade para ATIVO")
    void testAlterarDisponibilidadeSucesso() {

        LocalDateTime antes = LocalDateTime.now();

        try {
            Thread.sleep(10); // ← Aguarda 10ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        doNothing().when(authService)
                .autenticarTecnico(1L, "123456");

        when(tecnicoRepository.findById(1L))
                .thenReturn(Optional.of(tecnico));

        when(tecnicoRepository.save(tecnico))
                .thenReturn(tecnico);

        tecnicoService.alterarDisponibilidade(
                1L,
                "123456",
                DisponibilidadeStatus.ATIVO);

        assertEquals(
                DisponibilidadeStatus.ATIVO,
                tecnico.getDisponibilidade());

        assertTrue(
                tecnico.getDisponibilidadeAtualizadaEm().isAfter(antes));

        verify(tecnicoRepository).save(tecnico);
    }

    @Test
    @DisplayName("Não deve alterar disponibilidade com código inválido")
    void testAlterarDisponibilidadeCodigoInvalido() {

        doThrow(new CodigoDeAcessoInvalidoException())
                .when(authService)
                .autenticarTecnico(1L, "000000");

        assertThrows(
                CodigoDeAcessoInvalidoException.class,
                () -> tecnicoService.alterarDisponibilidade(
                        1L,
                        "000000",
                        DisponibilidadeStatus.ATIVO));

        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Atualizar dados não deve alterar disponibilidade")
    void testAtualizarNaoAlteraDisponibilidade() {

        tecnico.setDisponibilidade(DisponibilidadeStatus.DESCANSO);

        doNothing().when(authService)
                .autenticarTecnico(1L, "123456");

        when(tecnicoRepository.findById(1L))
                .thenReturn(Optional.of(tecnico));

        doNothing().when(modelMapper)
                .map(tecnicoDTO, tecnico);

        when(tecnicoRepository.save(tecnico))
                .thenReturn(tecnico);

        when(modelMapper.map(tecnico,
                TecnicoResponseDTO.class))
                .thenReturn(tecnicoResponseDTO);

        tecnicoService.atualizar(
                1L,
                "123456",
                tecnicoDTO);

        assertEquals(
                DisponibilidadeStatus.DESCANSO,
                tecnico.getDisponibilidade());
    }

    @Test
    @DisplayName("Deve permitir voltar para DESCANSO")
    void testVoltarParaDescanso() {

        tecnico.setDisponibilidade(DisponibilidadeStatus.ATIVO);

        doNothing().when(authService)
                .autenticarTecnico(1L, "123456");

        when(tecnicoRepository.findById(1L))
                .thenReturn(Optional.of(tecnico));

        tecnicoService.alterarDisponibilidade(
                1L,
                "123456",
                DisponibilidadeStatus.DESCANSO);

        assertEquals(
                DisponibilidadeStatus.DESCANSO,
                tecnico.getDisponibilidade());
    }

    @Test
    @DisplayName("Deve chamar processarTecnicoAtivo ao alterar disponibilidade para ATIVO")
    void testAlterarDisponibilidadeAtivoDisparaAtribuicao() {

        tecnico.setDisponibilidade(DisponibilidadeStatus.DESCANSO);
        tecnico.setDisponibilidadeAtualizadaEm(LocalDateTime.now().minusHours(1));
        tecnico.getEmpresasAprovadoras().add(empresa);

        doNothing().when(authService).autenticarTecnico(1L, "123456");
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        doNothing().when(atribuicaoService).processarTecnicoAtivo(tecnico);
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        tecnicoService.alterarDisponibilidade(1L, "123456", DisponibilidadeStatus.ATIVO);

        assertEquals(DisponibilidadeStatus.ATIVO, tecnico.getDisponibilidade());
        verify(atribuicaoService, times(1)).processarTecnicoAtivo(tecnico);
    }

    @Test
    @DisplayName("adicionarAprovacao: técnico ATIVO com empresa aprovada deve disparar atribuição")
    void testAdicionarAprovacaoTecnicoAtivoDisparaAtribuicao() {
        // técnico já ATIVO
        tecnico.setDisponibilidade(DisponibilidadeStatus.ATIVO);

        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        doNothing().when(atribuicaoService).processarTecnicoAtivo(tecnico);

        tecnicoService.adicionarAprovacao(1L, empresa);

        // o técnico deve ter a empresa na lista de aprovadoras
        assertTrue(tecnico.getEmpresasAprovadoras().contains(empresa));
        // como está ATIVO e agora tem empresa aprovadora, deve disparar
        verify(atribuicaoService, times(1)).processarTecnicoAtivo(tecnico);
    }

    @Test
    @DisplayName("adicionarAprovacao: técnico ATIVO sem nenhuma empresa aprovada não deve disparar atribuição")
    void testAdicionarAprovacaoTrocaDeReprovadoParaAprovado() {
        // técnico ATIVO mas sem empresa aprovadora ainda
        tecnico.setDisponibilidade(DisponibilidadeStatus.ATIVO);
        tecnico.getEmpresasReprovadoras().add(empresa); // empresa estava na lista de reprovadoras

        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        doNothing().when(atribuicaoService).processarTecnicoAtivo(tecnico);

        tecnicoService.adicionarAprovacao(1L, empresa);

        // empresa saiu de reprovadoras e entrou em aprovadoras
        assertFalse(tecnico.getEmpresasReprovadoras().contains(empresa));
        assertTrue(tecnico.getEmpresasAprovadoras().contains(empresa));
        // ATIVO + aprovadora → deve disparar
        verify(atribuicaoService, times(1)).processarTecnicoAtivo(tecnico);
    }

}