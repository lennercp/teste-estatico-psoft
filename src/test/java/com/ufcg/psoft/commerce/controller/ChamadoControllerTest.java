package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Chamados")
public class ChamadoControllerTest {

    private static final String URI = "/chamados";

    @Autowired MockMvc driver;
    @Autowired ObjectMapper objectMapper;

    @Autowired PagamentoRepository pagamentoRepository;
    @Autowired ClienteRepository clienteRepository;
    @Autowired EmpresaRepository empresaRepository;
    @Autowired ServicoRepository servicoRepository;
    @Autowired ChamadoRepository chamadoRepository;

    Cliente cliente;
    Empresa empresa;
    Servico servico;
    ChamadoPostPutRequestDTO criarDTO;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Teste")
                .endereco("Rua A")
                .codigo("123456")
                .planoAtual(TipoPlano.BASICO)
                .planoAgendado(TipoPlano.BASICO)
                .build());

        empresa = empresaRepository.save(Empresa.builder()
                .nomeFantasia("Empresa X")
                .cnpj("111")
                .codigoAcesso("123456")
                .endereco("Rua Empresa")
                .build());

        servico = servicoRepository.save(Servico.builder()
                .nome("Servico Basico")
                .descricao("Desc")
                .precoBase(100.0)
                .tipo(TipoServico.ELETRICA)
                .nivelUrgencia(NivelUrgencia.NORMAL)
                .tipoPlano(TipoPlano.BASICO)
                .duracaoEstimada(2)
                .ativo(true)
                .empresa(empresa)
                .build());

        criarDTO = ChamadoPostPutRequestDTO.builder()
                .cliente_id(cliente.getId())
                .empresaCnpj(empresa.getCnpj())
                .servico_id(servico.getId())
                .endereco("Endereco chamado")
                .build();
    }

    @AfterEach
    void tearDown() {
        pagamentoRepository.deleteAll();
        chamadoRepository.deleteAll();
        servicoRepository.deleteAll();
        clienteRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Nested
    class CriarChamado {

        @Test
        void criarChamadoValido() throws Exception {
            String json = driver.perform(post(URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(criarDTO)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ChamadoResponseDTO resp = objectMapper.readValue(json, ChamadoResponseDTO.class);
            assertNotNull(resp.getId());
        }

        @Test
        void criarChamadoNaoCliente() throws Exception {
            driver.perform(post(URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "EMPRESA")
                            .header("X-EMPRESA-CNPJ", empresa.getCnpj())
                            .header("X-ACCESS-CODE", empresa.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(criarDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class RecuperarChamado {

        Chamado chamado;

        @BeforeEach
        void criarChamado() {
            chamadoRepository.deleteAll();
            chamado = chamadoRepository.save(Chamado.builder()
                    .cliente(cliente)
                    .empresa(empresa)
                    .servico(servico)
                    .endereco("Rua A")
                    .build());
        }

        @Test
        void recuperarValido() throws Exception {
            driver.perform(get(URI + "/" + chamado.getId())
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo()))
                    .andExpect(status().isOk());
        }

        @Test
        void recuperarSemPermissao() throws Exception {
            Cliente outro = clienteRepository.save(Cliente.builder()
                    .nome("Outro")
                    .endereco("B")
                    .codigo("654321")
                    .planoAtual(TipoPlano.BASICO)
                    .planoAgendado(TipoPlano.BASICO)
                    .build());

            driver.perform(get(URI + "/" + chamado.getId())
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", outro.getId())
                            .header("X-ACCESS-CODE", outro.getCodigo()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void recuperarInexistente() throws Exception {
            driver.perform(get(URI + "/999")
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class AtualizarChamado {

        Chamado chamado;

        @BeforeEach
        void criarChamado() {
            chamado = chamadoRepository.save(Chamado.builder()
                    .cliente(cliente)
                    .empresa(empresa)
                    .servico(servico)
                    .endereco("Rua A")
                    .build());
        }

        @Test
        void atualizarValido() throws Exception {
            ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
            dto.setEndereco("Novo endereco");

            driver.perform(patch(URI + "/" + chamado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }

        @Test
        void atualizarSemPermissao() throws Exception {
            Cliente outro = clienteRepository.save(Cliente.builder()
                    .nome("Outro")
                    .endereco("B")
                    .codigo("654321")
                    .planoAtual(TipoPlano.BASICO)
                    .planoAgendado(TipoPlano.BASICO)
                    .build());

            ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
            dto.setEndereco("Novo");

            driver.perform(patch(URI + "/" + chamado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", outro.getId())
                            .header("X-ACCESS-CODE", outro.getCodigo())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeletarChamado {

        Chamado chamado;

        @BeforeEach
        void criarChamado() {
            chamado = chamadoRepository.save(Chamado.builder()
                    .cliente(cliente)
                    .empresa(empresa)
                    .servico(servico)
                    .endereco("Rua A")
                    .build());
        }

        @Test
        void deletarValido() throws Exception {
            driver.perform(delete(URI + "/" + chamado.getId())
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo()))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deletarSemPermissao() throws Exception {
            Cliente outro = clienteRepository.save(Cliente.builder()
                    .nome("Outro")
                    .endereco("B")
                    .codigo("654321")
                    .planoAtual(TipoPlano.BASICO)
                    .planoAgendado(TipoPlano.BASICO)
                    .build());

            driver.perform(delete(URI + "/" + chamado.getId())
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", outro.getId())
                            .header("X-ACCESS-CODE", outro.getCodigo()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class PagamentoChamado {

        Chamado chamado;

        @BeforeEach
        void criarChamado() {
            chamado = chamadoRepository.save(Chamado.builder()
                    .cliente(cliente)
                    .empresa(empresa)
                    .servico(servico)
                    .endereco("Rua A")
                    .build());
        }

        @Test
        void pagamentoValido() throws Exception {
            ChamadoPagamentoRequestDTO dto = new ChamadoPagamentoRequestDTO();
            dto.setMetodo(MeioPagamento.PIX);

            driver.perform(post(URI + "/" + chamado.getId() + "/pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNoContent());
        }

        @Test
        void pagamentoChamadoInexistente() throws Exception {
            ChamadoPagamentoRequestDTO dto = new ChamadoPagamentoRequestDTO();
            dto.setMetodo(MeioPagamento.PIX);

            driver.perform(post(URI + "/999/pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
