package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ServicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.dto.ServicoInteresseRequestDTO;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Serviços")
class ServicoControllerTest {

        static final String URI_BASE = "/empresas";

        @Autowired
        MockMvc driver;

        @Autowired
        EmpresaRepository empresaRepository;

        @Autowired
        ServicoRepository servicoRepository;

        @Autowired
        ClienteRepository clienteRepository;

        @Autowired
        InteresseRepository interesseRepository;

        ObjectMapper objectMapper = new ObjectMapper();

        Empresa empresaA;
        Empresa empresaB;
        Cliente cliente;

        Servico servicoEmpresaA;

        ServicoPostPutRequestDTO servicoPostPutRequestDTO;

        @BeforeEach
        void setup() {

                objectMapper.registerModule(new JavaTimeModule());

                empresaA = empresaRepository.save(
                                Empresa.builder()
                                                .cnpj("11111111111111")
                                                .nomeFantasia("Empresa A")
                                                .endereco("Rua A")
                                                .codigoAcesso("123456")
                                                .build());

                empresaB = empresaRepository.save(
                                Empresa.builder()
                                                .cnpj("22222222222222")
                                                .nomeFantasia("Empresa B")
                                                .endereco("Rua B")
                                                .codigoAcesso("654321")
                                                .build());

                servicoEmpresaA = servicoRepository.save(
                                Servico.builder()
                                                .nome("Instalação Elétrica")
                                                .nivelUrgencia(NivelUrgencia.NORMAL)
                                                .tipo(TipoServico.ELETRICA)
                                                .tipoPlano(TipoPlano.AMBOS)
                                                .descricao("Serviço elétrico")
                                                .precoBase(200.00)
                                                .duracaoEstimada(60)
                                                .empresa(empresaA)
                                                .ativo(true)
                                                .build());

                servicoPostPutRequestDTO = ServicoPostPutRequestDTO.builder()
                                .nome("Pintura Residencial")
                                .tipo(TipoServico.PINTURA)
                                .nivelUrgencia(NivelUrgencia.NORMAL)
                                .tipoPlano(TipoPlano.AMBOS)
                                .descricao("Pintura interna")
                                .precoBase(500.00)
                                .duracaoEstimada(120)
                                .build();

                cliente = clienteRepository.save(
                                Cliente.builder()
                                                .nome("Cliente Teste")
                                                .endereco("Rua Cliente")
                                                .codigo("123456")
                                                .planoAtual(TipoPlano.BASICO)
                                                .build());
        }

        @AfterEach
        void tearDown() {
                interesseRepository.deleteAll();
                servicoRepository.deleteAll();
                clienteRepository.deleteAll();
                empresaRepository.deleteAll();
        }

        @Nested
        @DisplayName("Conjunto de casos de criação de serviços")
        class CriacaoServico {

                @Test
                @DisplayName("Quando criamos serviço com empresa e código válidos")
                void quandoCriamosServicoValido() throws Exception {

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(objectMapper.writeValueAsString(servicoPostPutRequestDTO)))
                                        .andExpect(status().isCreated())
                                        .andDo(print());
                }

                @Test
                @DisplayName("Quando criamos serviço com nome já existente na empresa")
                void quandoCriamosServicoComNomeDuplicado() throws Exception {

                        servicoPostPutRequestDTO.setNome(servicoEmpresaA.getNome());

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(objectMapper.writeValueAsString(servicoPostPutRequestDTO)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando criamos serviço com tipo inválido")
                void quandoCriamosServicoTipoInvalido() throws Exception {

                        String jsonInvalido = """
                                        {
                                          "nome": "Servico Teste",
                                          "tipo": "INVALIDO",
                                          "descricao": "Teste",
                                          "precoBase": 100.00,
                                          "duracaoEstimada": 30
                                        }
                                        """;

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(jsonInvalido))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando tentamos criar serviço para empresa que não existe")
                void quandoCriamosServicoEmpresaNaoExiste() throws Exception {

                        String cnpjInexistente = "00000000000000";

                        driver.perform(post(URI_BASE + "/" + cnpjInexistente + "/servicos")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", "QUALQUER_CODIGO")
                                        .content(objectMapper.writeValueAsString(servicoPostPutRequestDTO)))
                                        .andExpect(status().isBadRequest());
                }

        }

        @Nested
        @DisplayName("Conjunto de casos de alteração de serviços")
        class AlteracaoServico {

                @Test
                @DisplayName("Quando alteramos serviço com código válido")
                void quandoAlteramosServicoValido() throws Exception {

                        servicoPostPutRequestDTO.setNome("Serviço Atualizado");

                        String responseJson = driver.perform(
                                        put(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/"
                                                        + servicoEmpresaA.getId())
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                                        .content(objectMapper
                                                                        .writeValueAsString(servicoPostPutRequestDTO)))
                                        .andExpect(status().isOk())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(StandardCharsets.UTF_8);

                        ServicoResponseDTO response = objectMapper.readValue(responseJson, ServicoResponseDTO.class);

                        assertEquals("Serviço Atualizado", response.getNome());
                }

                @Test
                @DisplayName("Quando empresa tenta alterar serviço de outra empresa")
                void quandoEmpresaAlteraServicoDeOutraEmpresa() throws Exception {

                        driver.perform(
                                        put(URI_BASE + "/" + empresaB.getCnpj() + "/servicos/"
                                                        + servicoEmpresaA.getId())
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .param("codigoAcesso", empresaB.getCodigoAcesso())
                                                        .content(objectMapper
                                                                        .writeValueAsString(servicoPostPutRequestDTO)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando tentamos alterar serviço inexistente")
                void quandoAlteramosServicoInexistente() throws Exception {

                        driver.perform(
                                        put(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/999")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                                        .content(objectMapper
                                                                        .writeValueAsString(servicoPostPutRequestDTO)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de consulta de serviços")
        class ConsultaServico {

                @Test
                @DisplayName("Quando consultamos serviço existente")
                void quandoConsultamosServicoExistente() throws Exception {

                        driver.perform(
                                        get(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/"
                                                        + servicoEmpresaA.getId()))
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("Quando consultamos serviço inexistente")
                void quandoConsultamosServicoInexistente() throws Exception {

                        driver.perform(
                                        get(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/999"))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de adicionar interesse")
        class AdicionarInteresse {

                @Test
                @DisplayName("Quando adicionamos interesse com sucesso")
                void quandoAdicionamosInteresseSucesso() throws Exception {
                        // Tornar servico indisponivel
                        servicoEmpresaA.setAtivo(false);
                        servicoRepository.save(servicoEmpresaA);

                        ServicoInteresseRequestDTO interesseDTO = ServicoInteresseRequestDTO.builder()
                                        .id(cliente.getId())
                                        .codigoAcesso(cliente.getCodigo())
                                        .build();

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/" + servicoEmpresaA.getId()
                                        + "/interesse")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(objectMapper.writeValueAsString(interesseDTO)))
                                        .andExpect(status().isCreated());
                }

                @Test
                @DisplayName("Quando tentamos adicionar interesse em serviço disponível")
                void quandoAdicionamosInteresseServicoDisponivel() throws Exception {
                        // Servico ja eh ativo por padrao no setup

                        ServicoInteresseRequestDTO interesseDTO = ServicoInteresseRequestDTO.builder()
                                        .id(cliente.getId())
                                        .codigoAcesso(cliente.getCodigo())
                                        .build();

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/" + servicoEmpresaA.getId()
                                        + "/interesse")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(objectMapper.writeValueAsString(interesseDTO)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando tentamos adicionar interesse em serviço inexistente")
                void quandoAdicionamosInteresseServicoInexistente() throws Exception {
                        ServicoInteresseRequestDTO interesseDTO = ServicoInteresseRequestDTO.builder()
                                        .id(cliente.getId())
                                        .codigoAcesso(cliente.getCodigo())
                                        .build();

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/99999/interesse")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(objectMapper.writeValueAsString(interesseDTO)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando tentamos adicionar interesse com cliente inexistente")
                void quandoAdicionamosInteresseClienteInexistente() throws Exception {
                        servicoEmpresaA.setAtivo(false);
                        servicoRepository.save(servicoEmpresaA);

                        ServicoInteresseRequestDTO interesseDTO = ServicoInteresseRequestDTO.builder()
                                        .id(99999L)
                                        .codigoAcesso("123456")
                                        .build();

                        driver.perform(post(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/" + servicoEmpresaA.getId()
                                        + "/interesse")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .content(objectMapper.writeValueAsString(interesseDTO)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("Conjunto de casos de alteração de disponibilidade (US10)")
        class AlteracaoDisponibilidade {

                @Test
                @DisplayName("Quando alteramos a disponibilidade com sucesso")
                void quandoAlteramosDisponibilidadeSucesso() throws Exception {

                        driver.perform(patch(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/"
                                        + servicoEmpresaA.getId() + "/disponibilidade")
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .param("disponivel", "false"))
                                        .andExpect(status().isOk());

                        Servico servicoAtualizado = servicoRepository.findById(servicoEmpresaA.getId()).get();
                        assertFalse(servicoAtualizado.getAtivo(), "O serviço deveria estar inativo (false)");
                }

                @Test
                @DisplayName("Quando tentamos alterar a disponibilidade com código inválido")
                void quandoAlteramosDisponibilidadeCodigoInvalido() throws Exception {

                        driver.perform(patch(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/"
                                        + servicoEmpresaA.getId() + "/disponibilidade")
                                        .param("codigoAcesso", "000000")
                                        .param("disponivel", "false"))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando tentamos alterar a disponibilidade de um serviço de outra empresa")
                void quandoAlteramosDisponibilidadeServicoDeOutraEmpresa() throws Exception {

                        driver.perform(patch(URI_BASE + "/" + empresaB.getCnpj() + "/servicos/"
                                        + servicoEmpresaA.getId() + "/disponibilidade")
                                        .param("codigoAcesso", empresaB.getCodigoAcesso())
                                        .param("disponivel", "false"))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("Quando tentamos alterar a disponibilidade de um serviço inexistente")
                void quandoAlteramosDisponibilidadeDeServicoInexistente() throws Exception {

                        driver.perform(patch(URI_BASE + "/" + empresaA.getCnpj() + "/servicos/99999/disponibilidade")
                                        .param("codigoAcesso", empresaA.getCodigoAcesso())
                                        .param("disponivel", "false"))
                                        .andExpect(status().isBadRequest());
                }
        }

}