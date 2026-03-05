package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.TipoPlano;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.HistoricoAssinaturaRepository;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.Servico;
import com.ufcg.psoft.commerce.model.TipoServico;
import com.ufcg.psoft.commerce.model.NivelUrgencia;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
class ClienteControllerTests {

        static final String URL_CLIENTES = "/clientes";

        @Autowired
        MockMvc driver;

        @Autowired
        ClienteRepository clienteRepository;

        @Autowired
        HistoricoAssinaturaRepository historicoAssinaturaRepository;

        ObjectMapper objectMapper = new ObjectMapper();

        Cliente cliente;

        ClientePostPutRequestDTO clientePostPutRequestDTO;

        @Autowired
        EmpresaRepository empresaRepository;

        @Autowired
        ServicoRepository servicoRepository;

        @BeforeEach
        void setup() {
                // Object Mapper suporte para LocalDateTime
                objectMapper.registerModule(new JavaTimeModule());
                cliente = clienteRepository.save(Cliente.builder()
                                .nome("Cliente Um da Silva")
                                .endereco("Rua dos Testes, 123")
                                .codigo("123456")
                                .planoAtual(TipoPlano.BASICO)
                                .planoAgendado(TipoPlano.BASICO)
                                .build());
                clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                                .nome(cliente.getNome())
                                .endereco(cliente.getEndereco())
                                .codigo(cliente.getCodigo())
                                .planoAgendado(cliente.getPlanoAtual())
                                .build();
        }

        @AfterEach
        void tearDown() {
                historicoAssinaturaRepository.deleteAll();
                servicoRepository.deleteAll();
                clienteRepository.deleteAll();
                empresaRepository.deleteAll();
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação de nome")
        class ClienteVerificacaoNome {

                @Test
                @DisplayName("Quando recuperamos um cliente com dados válidos")
                void quandoRecuperamosNomeDoClienteValido() throws Exception {

                        // Act
                        String responseJsonString = driver.perform(get(URL_CLIENTES + "/" + cliente.getId()))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertEquals("Cliente Um da Silva", resultado.getNome());
                }

                @Test
                @DisplayName("Quando alteramos o nome do cliente com dados válidos")
                void quandoAlteramosNomeDoClienteValido() throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setNome("Cliente Um Alterado");

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertEquals("Cliente Um Alterado", resultado.getNome());
                }

                @Test
                @DisplayName("Quando alteramos o nome do cliente nulo")
                void quandoAlteramosNomeDoClienteNulo() throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setNome(null);

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                                        () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0)));
                }

                @Test
                @DisplayName("Quando alteramos o nome do cliente vazio")
                void quandoAlteramosNomeDoClienteVazio() throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setNome("");

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                                        () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0)));
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação do endereço")
        class ClienteVerificacaoEndereco {

                @Test
                @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
                void quandoAlteramosEnderecoDoClienteValido() throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setEndereco("Endereco Alterado");

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.ClienteResponseDTOBuilder.class).build();

                        // Assert
                        assertEquals("Endereco Alterado", resultado.getEndereco());
                }

                @Test
                @DisplayName("Quando alteramos o endereço do cliente nulo")
                void quandoAlteramosEnderecoDoClienteNulo() throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setEndereco(null);

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                                        () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0)));
                }

                @Test
                @DisplayName("Quando alteramos o endereço do cliente vazio")
                void quandoAlteramosEnderecoDoClienteVazio() throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setEndereco("");

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                                        () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0)));
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação do código de acesso")
        class ClienteVerificacaoCodigoAcesso {

                static Stream<Arguments> codigosInvalidos() {
                        return Stream.of(
                                        Arguments.of(null, "Codigo de acesso obrigatorio"),
                                        Arguments.of("1234567",
                                                        "Codigo de acesso deve ter exatamente 6 digitos numericos"),
                                        Arguments.of("12345",
                                                        "Codigo de acesso deve ter exatamente 6 digitos numericos"),
                                        Arguments.of("a*c4e@",
                                                        "Codigo de acesso deve ter exatamente 6 digitos numericos"));
                }

                @ParameterizedTest
                @MethodSource("codigosInvalidos")
                @DisplayName("Quando alteramos o código de acesso do cliente com valor inválido")
                void quandoAlteramosCodigoAcessoDoClienteInvalido(String codigoInvalido, String mensagemEsperada)
                                throws Exception {
                        // Arrange
                        clientePostPutRequestDTO.setCodigo(codigoInvalido);

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                                        () -> assertEquals(mensagemEsperada, resultado.getErrors().get(0)));
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
        class ClienteVerificacaoFluxosBasicosApiRest {

                @Test
                @DisplayName("Quando buscamos por todos clientes salvos")
                void quandoBuscamosPorTodosClienteSalvos() throws Exception {
                        // Arrange
                        // Vamos ter 3 clientes no banco
                        Cliente cliente1 = Cliente.builder()
                                        .nome("Cliente Dois Almeida")
                                        .endereco("Av. da Pits A, 100")
                                        .codigo("246810")
                                        .planoAtual(TipoPlano.BASICO)
                                        .planoAgendado(TipoPlano.BASICO)
                                        .build();
                        Cliente cliente2 = Cliente.builder()
                                        .nome("Cliente Três Lima")
                                        .endereco("Distrito dos Testadores, 200")
                                        .codigo("135790")
                                        .planoAtual(TipoPlano.PREMIUM)
                                        .planoAgendado(TipoPlano.PREMIUM)
                                        .build();
                        clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

                        // Act
                        String responseJsonString = driver.perform(get(URL_CLIENTES)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isOk()) // Codigo 200
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        List<ClienteResponseDTO> resultado = objectMapper.readValue(responseJsonString,
                                        new TypeReference<>() {
                                        });

                        // Assert
                        assertAll(
                                        () -> assertEquals(3, resultado.size()));
                }

                @Test
                @DisplayName("Quando buscamos um cliente salvo pelo id")
                void quandoBuscamosPorUmClienteSalvo() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(get(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isOk()) // Codigo 200
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        new TypeReference<>() {
                                        });

                        // Assert
                        assertAll(
                                        () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                                        () -> assertEquals(cliente.getNome(), resultado.getNome()));
                }

                @Test
                @DisplayName("Quando buscamos um cliente inexistente")
                void quandoBuscamosPorUmClienteInexistente() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(get(URL_CLIENTES + "/" + 999999999)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest()) // Codigo 400
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage()));
                }

                @Test
                @DisplayName("Quando criamos um novo cliente com dados válidos")
                void quandoCriarClienteValido() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(post(URL_CLIENTES)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isCreated()) // Codigo 201
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertAll(
                                        () -> assertNotNull(resultado.getId()),
                                        () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()));

                }

                @Test
                @DisplayName("Quando alteramos o cliente com dados válidos")
                void quandoAlteramosClienteValido() throws Exception {
                        // Arrange
                        Long clienteId = cliente.getId();

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isOk()) // Codigo 200
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals(clienteId, resultado.getId().longValue()),
                                        () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()));
                }

                @Test
                @DisplayName("Quando alteramos o cliente inexistente")
                void quandoAlteramosClienteInexistente() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + 99999L)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest()) // Codigo 400
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage()));
                }

                @Test
                @DisplayName("Quando alteramos o cliente passando código de acesso inválido")
                void quandoAlteramosClienteCodigoAcessoInvalido() throws Exception {
                        // Arrange
                        Long clienteId = cliente.getId();

                        // Act
                        String responseJsonString = driver.perform(put(URL_CLIENTES + "/" + clienteId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", "invalido")
                                        .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                                        .andExpect(status().isBadRequest()) // Codigo 400
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage()));
                }

                @Test
                @DisplayName("Quando excluímos um cliente salvo")
                void quandoExcluimosClienteValido() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(delete(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo()))
                                        .andExpect(status().isNoContent()) // Codigo 204
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        // Assert
                        assertTrue(responseJsonString.isBlank());
                }

                @Test
                @DisplayName("Quando excluímos um cliente inexistente")
                void quandoExcluimosClienteInexistente() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(delete(URL_CLIENTES + "/" + 999999)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo()))
                                        .andExpect(status().isBadRequest()) // Codigo 400
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage()));
                }

                @Test
                @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
                void quandoExcluimosClienteCodigoAcessoInvalido() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(delete(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", "invalido"))
                                        .andExpect(status().isBadRequest()) // Codigo 400
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage()));
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação de alterar parcial")
        class ClienteVerificacaoAlterarParcial {

                @Test
                @DisplayName("Quando alteramos o nome do cliente com dados válidos")
                void quandoAlteramosNomeDoClienteValido() throws Exception {
                        // Arrange
                        String nomeAlterado = "Nome Parcial Alterado";
                        String requestBody = objectMapper.writeValueAsString(ClientePostPutRequestDTO.builder()
                                        .nome(nomeAlterado)
                                        .build());

                        // Act
                        String responseJsonString = driver.perform(patch(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(requestBody))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertEquals(nomeAlterado, resultado.getNome());
                        assertEquals(cliente.getEndereco(), resultado.getEndereco());
                }

                @Test
                @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
                void quandoAlteramosEnderecoDoClienteValido() throws Exception {
                        // Arrange
                        String enderecoAlterado = "Endereco Parcial Alterado";
                        String requestBody = objectMapper.writeValueAsString(ClientePostPutRequestDTO.builder()
                                        .endereco(enderecoAlterado)
                                        .build());

                        // Act
                        String responseJsonString = driver.perform(patch(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(requestBody))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertEquals(enderecoAlterado, resultado.getEndereco());
                        assertEquals(cliente.getNome(), resultado.getNome());
                }

                @Test
                @DisplayName("Quando alteramos o plano do cliente com dados válidos")
                void quandoAlteramosPlanoDoClienteValido() throws Exception {
                        // Arrange
                        TipoPlano novoPlano = TipoPlano.PREMIUM;

                        String requestBody = "{\"plano\": \"PREMIUM\"}";

                        // Act
                        String responseJsonString = driver.perform(patch(URL_CLIENTES + "/" + cliente.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigo", cliente.getCodigo())
                                        .content(requestBody))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertAll(
                                        () -> assertEquals(TipoPlano.BASICO, resultado.getPlanoAtual()),
                                        () -> assertEquals(novoPlano, clienteRepository.findById(cliente.getId()).get()
                                                        .getPlanoAgendado()));
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação de próximo ciclo de cobrança")
        class ClienteVerificacaoProxCicloCobranca {

                @Test
                @DisplayName("Quando adiantamos o próximo ciclo de cobrança com dados válidos")
                void quandoAdiantamosProximoCicloCobrancaValido() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver
                                        .perform(patch(URL_CLIENTES + "/" + cliente.getId() + "/proxCiclo"))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                                        ClienteResponseDTO.class);

                        // Assert
                        assertEquals(cliente.getId(), resultado.getId());
                }

                @Test
                @DisplayName("Quando adiantamos o próximo ciclo de cobrança de cliente inexistente")
                void quandoAdiantamosProximoCicloCobrancaInexistente() throws Exception {
                        // Arrange
                        // nenhuma necessidade além do setup()

                        // Act
                        String responseJsonString = driver.perform(patch(URL_CLIENTES + "/" + 99999L + "/proxCiclo"))
                                        .andExpect(status().isBadRequest())
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();

                        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                        // Assert
                        assertEquals("O cliente consultado nao existe!", resultado.getMessage());
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação de Catálogo de Serviços (US7)")
        class ClienteCatalogoServicos {

                private Empresa empresa;
                private Servico servicoBasico;
                private Servico servicoPremium;

                @BeforeEach
                void setupCatalogo() {
                        empresa = empresaRepository.save(Empresa.builder()
                                        .nomeFantasia("HomeFix Reparos")
                                        .cnpj("12.345.678/0001-99")
                                        .codigoAcesso("123456")
                                        .endereco("Av. Comercial, 1000")
                                        .build());

                        servicoBasico = servicoRepository.save(Servico.builder()
                                        .nome("Troca de Lampada")
                                        .descricao("Troca simples")
                                        .precoBase(50.0)
                                        .tipo(TipoServico.ELETRICA)
                                        .nivelUrgencia(NivelUrgencia.NORMAL)
                                        .tipoPlano(TipoPlano.BASICO)
                                        .duracaoEstimada(1)
                                        .ativo(true)
                                        .empresa(empresa)
                                        .build());

                        servicoPremium = servicoRepository.save(Servico.builder()
                                        .nome("Instalação Industrial")
                                        .descricao("Serviço complexo")
                                        .precoBase(500.0)
                                        .tipo(TipoServico.ELETRICA)
                                        .nivelUrgencia(NivelUrgencia.URGENTE)
                                        .tipoPlano(TipoPlano.PREMIUM)
                                        .duracaoEstimada(5)
                                        .ativo(true)
                                        .empresa(empresa)
                                        .build());
                }

                @Test
                @DisplayName("Cliente BASICO deve ver apenas serviços básicos")
                void quandoClienteBasicoConsultaCatalogo() throws Exception {

                        String responseJson = driver.perform(get(URL_CLIENTES + "/" + cliente.getId() + "/servicos")
                                        .param("codigoAcesso", cliente.getCodigo())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn().getResponse().getContentAsString();

                        List<ServicoResponseDTO> resultado = objectMapper.readValue(responseJson,
                                        new TypeReference<>() {
                                        });

                        assertAll(
                                        () -> assertEquals(1, resultado.size()),
                                        () -> assertEquals("Troca de Lampada", resultado.get(0).getNome()));
                }

                @Test
                @DisplayName("Cliente PREMIUM deve ver todos os serviços")
                void quandoClientePremiumConsultaCatalogo() throws Exception {
                        cliente.setPlanoAtual(TipoPlano.PREMIUM);
                        clienteRepository.save(cliente);

                        String responseJson = driver.perform(get(URL_CLIENTES + "/" + cliente.getId() + "/servicos")
                                        .param("codigoAcesso", cliente.getCodigo())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn().getResponse().getContentAsString();

                        List<ServicoResponseDTO> resultado = objectMapper.readValue(responseJson,
                                        new TypeReference<>() {
                                        });

                        assertAll(
                                        () -> assertEquals(2, resultado.size()));
                }

                @Test
                @DisplayName("Deve filtrar serviços por preço máximo")
                void quandoFiltrarPorPrecoMaximo() throws Exception {
                        cliente.setPlanoAtual(TipoPlano.PREMIUM);
                        clienteRepository.save(cliente);

                        String responseJson = driver.perform(get(URL_CLIENTES + "/" + cliente.getId() + "/servicos")
                                        .param("codigoAcesso", cliente.getCodigo())
                                        .param("precoMax", "100.0")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn().getResponse().getContentAsString();

                        List<ServicoResponseDTO> resultado = objectMapper.readValue(responseJson,
                                        new TypeReference<>() {
                                        });

                        assertEquals(1, resultado.size());
                        assertEquals("Troca de Lampada", resultado.get(0).getNome());
                }
        }

}
