package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.ServicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.exception.ServicoJaExisteException;
import com.ufcg.psoft.commerce.exception.ServicoNaoExisteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import com.ufcg.psoft.commerce.service.servico.ServicoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Service de Serviço")
class ServicoServiceImplTest {

        @Mock
        ServicoRepository servicoRepository;

        @Mock
        EmpresaRepository empresaRepository;

        @Mock
        ModelMapper modelMapper;

        @Mock
        AuthService authService;

        @Mock
        InteresseRepository interesseRepository;

        @InjectMocks
        ServicoServiceImpl servicoService;

        static final String CNPJ = "12345678910111";
        static final String CODIGO_VALIDO = "123456";
        static final String CODIGO_INVALIDO = "000000";
        static final Long SERVICO_ID = 1L;

        Empresa empresa;
        Servico servico;
        ServicoPostPutRequestDTO requestDTO;
        ServicoResponseDTO responseDTO;

        @BeforeEach
        void setup() {

                empresa = Empresa.builder()
                                .cnpj(CNPJ)
                                .nomeFantasia("Empresa Teste")
                                .codigoAcesso(CODIGO_VALIDO)
                                .build();

                servico = Servico.builder()
                                .id(SERVICO_ID)
                                .nome("Serviço Teste")
                                .tipo(TipoServico.PINTURA)
                                .nivelUrgencia(NivelUrgencia.NORMAL)
                                .descricao("Descrição do serviço")
                                .precoBase(500.0)
                                .tipoPlano(TipoPlano.AMBOS)
                                .duracaoEstimada(120)
                                .ativo(true)
                                .empresa(empresa)
                                .build();

                requestDTO = ServicoPostPutRequestDTO.builder()
                                .nome("Serviço Teste")
                                .tipo(TipoServico.PINTURA)
                                .nivelUrgencia(NivelUrgencia.NORMAL)
                                .descricao("Descrição do serviço")
                                .precoBase(500.0)
                                .tipoPlano(TipoPlano.AMBOS)
                                .duracaoEstimada(120)
                                .build();

                responseDTO = new ServicoResponseDTO(servico);
        }

        @Test
        @DisplayName("Quando empresa cria serviço válido")
        void quandoCriamosServicoValido() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.existsByNomeAndEmpresa(
                                requestDTO.getNome(), empresa))
                                .thenReturn(false);

                when(modelMapper.map(requestDTO, Servico.class))
                                .thenReturn(servico);

                when(modelMapper.map(servico, ServicoResponseDTO.class))
                                .thenReturn(responseDTO);

                ServicoResponseDTO resultado = servicoService.criar(CNPJ, CODIGO_VALIDO, requestDTO);

                assertNotNull(resultado);
                assertEquals("Serviço Teste", resultado.getNome());

                verify(servicoRepository).save(servico);
        }

        @Test
        @DisplayName("Quando empresa cria serviço com código inválido")
        void quandoCriamosServicoCodigoInvalido() {

                doThrow(CodigoDeAcessoInvalidoException.class)
                                .when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_INVALIDO);

                assertThrows(CodigoDeAcessoInvalidoException.class,
                                () -> servicoService.criar(CNPJ, CODIGO_INVALIDO, requestDTO));

                verify(servicoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Quando empresa tenta criar serviço que já existe")
        void quandoCriamosServicoJaExistente() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.existsByNomeAndEmpresa(
                                requestDTO.getNome(), empresa))
                                .thenReturn(true);

                assertThrows(ServicoJaExisteException.class,
                                () -> servicoService.criar(CNPJ, CODIGO_VALIDO, requestDTO));

                verify(servicoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Quando tentamos criar serviço para empresa que não existe")
        void quandoCriamosServicoEmpresaNaoExiste() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.empty());

                assertThrows(EmpresaNaoExisteException.class,
                                () -> servicoService.criar(CNPJ, CODIGO_VALIDO, requestDTO));

                verify(servicoRepository, never()).save(any());
                verify(servicoRepository, never())
                                .existsByNomeAndEmpresa(any(), any());
                verify(modelMapper, never())
                                .map(any(), eq(Servico.class));
        }

        @Test
        @DisplayName("Quando alteramos serviço válido")
        void quandoAlteramosServicoValido() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa))
                                .thenReturn(Optional.of(servico));

                doAnswer(invocation -> {
                        ServicoPostPutRequestDTO dto = invocation.getArgument(0);
                        Servico ent = invocation.getArgument(1);

                        ent.setNome(dto.getNome());
                        ent.setTipo(dto.getTipo());
                        ent.setNivelUrgencia(dto.getNivelUrgencia());
                        ent.setDescricao(dto.getDescricao());
                        ent.setPrecoBase(dto.getPrecoBase());
                        ent.setTipoPlano(dto.getTipoPlano());
                        ent.setDuracaoEstimada(dto.getDuracaoEstimada());

                        return null;
                }).when(modelMapper).map(any(ServicoPostPutRequestDTO.class), any(Servico.class));

                when(modelMapper.map(servico, ServicoResponseDTO.class))
                                .thenReturn(responseDTO);

                ServicoResponseDTO resultado = servicoService.alterar(CNPJ, CODIGO_VALIDO, SERVICO_ID, requestDTO);

                assertNotNull(resultado);

                verify(servicoRepository).save(servico);
        }

        @Test
        @DisplayName("Quando tentamos alterar serviço inexistente")
        void quandoAlteramosServicoInexistente() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa))
                                .thenReturn(Optional.empty());

                assertThrows(ServicoNaoExisteException.class,
                                () -> servicoService.alterar(CNPJ, CODIGO_VALIDO, SERVICO_ID, requestDTO));
        }

        @Test
        @DisplayName("Quando buscamos serviço válido")
        void quandoBuscamosServicoValido() {

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa))
                                .thenReturn(Optional.of(servico));

                when(modelMapper.map(servico, ServicoResponseDTO.class))
                                .thenReturn(responseDTO);

                ServicoResponseDTO resultado = servicoService.buscar(CNPJ, SERVICO_ID);

                assertEquals(SERVICO_ID, resultado.getId());
        }

        @Test
        @DisplayName("Quando listamos serviços da empresa")
        void quandoListamosServicosEmpresa() {

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.findByEmpresa(empresa))
                                .thenReturn(List.of(servico));

                when(modelMapper.map(servico, ServicoResponseDTO.class))
                                .thenReturn(responseDTO);

                List<ServicoResponseDTO> resultado = servicoService.listar(CNPJ);

                assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Quando removemos serviço válido")
        void quandoRemovemosServicoValido() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa))
                                .thenReturn(Optional.of(servico));

                servicoService.remover(CNPJ, CODIGO_VALIDO, SERVICO_ID);

                verify(servicoRepository).delete(servico);
        }

        @Test
        @DisplayName("Quando tentamos remover serviço que não existe")
        void quandoRemovemosServicoInvalido() {

                doNothing().when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_VALIDO);

                when(empresaRepository.findByCnpj(CNPJ))
                                .thenReturn(Optional.of(empresa));

                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa))
                                .thenReturn(Optional.empty());

                assertThrows(ServicoNaoExisteException.class,
                                () -> servicoService.remover(CNPJ, CODIGO_VALIDO, SERVICO_ID));

                verify(servicoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Quando tentamos remover serviço com código de acesso inválido")
        void quandoRemovemosServicoComCodigoAcessoInvalido() {

                doThrow(CodigoDeAcessoInvalidoException.class)
                                .when(authService)
                                .autenticarEmpresa(CNPJ, CODIGO_INVALIDO);

                assertThrows(CodigoDeAcessoInvalidoException.class,
                                () -> servicoService.remover(CNPJ, CODIGO_INVALIDO, SERVICO_ID));

                verify(empresaRepository, never()).findByCnpj(any());
                verify(servicoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade para ativo, deve notificar priorizando Premium")
        void quandoAlteramosDisponibilidadeAtivandoComInteresses() {
                servico.setAtivo(false);

                Cliente clientePremium = Cliente.builder().nome("João").planoAtual(TipoPlano.PREMIUM).build();
                Cliente clienteBasico = Cliente.builder().nome("Maria").planoAtual(TipoPlano.BASICO).build();

                Interesse intBasico = new Interesse(clienteBasico, servico);
                intBasico.setDataInteresse(java.time.LocalDateTime.now().minusDays(2));

                Interesse intPremium = new Interesse(clientePremium, servico);
                intPremium.setDataInteresse(java.time.LocalDateTime.now().minusDays(1));

                List<Interesse> interesses = new java.util.ArrayList<>(List.of(intBasico, intPremium));

                doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_VALIDO);
                when(empresaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(empresa));
                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa)).thenReturn(Optional.of(servico));
                when(interesseRepository.findByServicoAndNotificadoFalse(servico)).thenReturn(interesses);
                when(modelMapper.map(servico, ServicoResponseDTO.class)).thenReturn(responseDTO);

                servicoService.alterarDisponibilidade(CNPJ, CODIGO_VALIDO, SERVICO_ID, true);

                assertTrue(servico.getAtivo());

                org.mockito.ArgumentCaptor<List> captor = org.mockito.ArgumentCaptor.forClass(List.class);
                verify(interesseRepository).saveAll(captor.capture());

                List<Interesse> salvos = captor.getValue();
                assertEquals(2, salvos.size());

                assertEquals(TipoPlano.PREMIUM, salvos.get(0).getCliente().getPlanoAtual());
                assertTrue(salvos.get(0).isNotificado());
                assertTrue(salvos.get(1).isNotificado());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade, não deve notificar se já estava ativo")
        void quandoAlteramosDisponibilidadeSemMudarEstado() {
                servico.setAtivo(true);

                doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_VALIDO);
                when(empresaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(empresa));
                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa)).thenReturn(Optional.of(servico));
                when(modelMapper.map(servico, ServicoResponseDTO.class)).thenReturn(responseDTO);

                servicoService.alterarDisponibilidade(CNPJ, CODIGO_VALIDO, SERVICO_ID, true);

                verify(interesseRepository, never()).findByServicoAndNotificadoFalse(any());
                verify(interesseRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade para inativo, NÃO deve notificar ninguém")
        void quandoAlteramosParaInativoNaoNotifica() {
                servico.setAtivo(true);

                doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_VALIDO);
                when(empresaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(empresa));
                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa)).thenReturn(Optional.of(servico));
                when(modelMapper.map(servico, ServicoResponseDTO.class)).thenReturn(responseDTO);

                servicoService.alterarDisponibilidade(CNPJ, CODIGO_VALIDO, SERVICO_ID, false);

                assertFalse(servico.getAtivo(), "O serviço deve ter ficado inativo");

                verify(interesseRepository, never()).findByServicoAndNotificadoFalse(any());
                verify(interesseRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Quando tentamos alterar disponibilidade de um serviço que não existe")
        void quandoAlteramosDisponibilidadeServicoInexistente() {
                doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_VALIDO);
                when(empresaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(empresa));
                when(servicoRepository.findByIdAndEmpresa(999L, empresa)).thenReturn(Optional.empty());

                assertThrows(ServicoNaoExisteException.class,
                                () -> servicoService.alterarDisponibilidade(CNPJ, CODIGO_VALIDO, 999L, false));
        }

        @Test
        @DisplayName("Quando ativamos um serviço, mas a fila de interessados está vazia")
        void quandoAtivamosServicoComFilaVazia() {
                // Arrange
                servico.setAtivo(false);

                doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_VALIDO);
                when(empresaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(empresa));
                when(servicoRepository.findByIdAndEmpresa(SERVICO_ID, empresa)).thenReturn(Optional.of(servico));

                when(interesseRepository.findByServicoAndNotificadoFalse(servico))
                                .thenReturn(new java.util.ArrayList<>());
                when(modelMapper.map(servico, ServicoResponseDTO.class)).thenReturn(responseDTO);

                servicoService.alterarDisponibilidade(CNPJ, CODIGO_VALIDO, SERVICO_ID, true);

                assertTrue(servico.getAtivo());
                verify(interesseRepository).saveAll(anyList());
        }

}