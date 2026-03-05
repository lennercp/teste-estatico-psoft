package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
import com.ufcg.psoft.commerce.repository.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @Autowired TecnicoRepository tecnicoRepository;

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
                .clienteId(cliente.getId())
                .empresaCnpj(empresa.getCnpj())
                .servicoId(servico.getId())
                .statusAcao("")
                .endereco("Endereco chamado")
                .build();
    }

    @AfterEach
    void tearDown() {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
                    .dataCriacao(LocalDateTime.now())
                    .status(StatusChamado.RECEBIDO)
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
                    .dataCriacao(LocalDateTime.now())
                    .status(StatusChamado.RECEBIDO)
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
                    .dataCriacao(LocalDateTime.now())
                    .status(StatusChamado.RECEBIDO)
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
                    .dataCriacao(LocalDateTime.now())
                    .status(StatusChamado.RECEBIDO)
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

    @Nested
    class TestesDeEstadoECancelamento {

        Chamado chamado;

        @BeforeEach
        void prepararChamado() {
            chamado = chamadoRepository.save(Chamado.builder()
                    .cliente(cliente)
                    .empresa(empresa)
                    .servico(servico)
                    .endereco("Rua Teste")
                    .dataCriacao(LocalDateTime.now())
                    .status(StatusChamado.RECEBIDO)
                    .build());
        }

        @Test
        @DisplayName("Deve avançar de RECEBIDO para EM_ANALISE com sucesso pela Empresa")
        void avancarStatusSucesso() throws Exception {
            ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
            dto.setStatusAcao("AVANCAR");
            dto.setCodigo(empresa.getCodigoAcesso());

            driver.perform(patch(URI + "/" + chamado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "EMPRESA")
                            .header("X-EMPRESA-CNPJ", empresa.getCnpj())
                            .header("X-ACCESS-CODE", empresa.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("EM_ANALISE"));
        }

        @Test
        @DisplayName("Deve atribuir técnico ao avançar para ATENDIMENTO")
        void atribuirTecnicoEAvancar() throws Exception {

            chamado.setStatus(StatusChamado.AGUARDANDO_TECNICO);
            chamadoRepository.save(chamado);

            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Tecnico Especialista")
                    .especialidade("Elétrica")
                    .placaVeiculo("ABC-1234")
                    .tipoVeiculo("Carro")
                    .corVeiculo("Branco")
                    .codigoAcesso("111222")
                    .build();

            tecnico = tecnicoRepository.save(tecnico);

            ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
            dto.setStatusAcao("AVANCAR");
            dto.setTecnicoId(tecnico.getId());
            dto.setCodigo(empresa.getCodigoAcesso());

            driver.perform(patch(URI + "/" + chamado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "EMPRESA")
                            .header("X-EMPRESA-CNPJ", empresa.getCnpj())
                            .header("X-ACCESS-CODE", empresa.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ATENDIMENTO"))
                    .andExpect(jsonPath("$.tecnico_id").value(tecnico.getId()));

            Thread.sleep(200);  // ← ADICIONE AQUI também
        }
        @Test
        @DisplayName("Deve falhar ao cancelar chamado que já está em ATENDIMENTO")
        void erroAoCancelarChamadoEmAtendimento() throws Exception {

            chamado.setStatus(StatusChamado.ATENDIMENTO);
            chamadoRepository.save(chamado);

            driver.perform(delete(URI + "/" + chamado.getId())
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", cliente.getId())
                            .header("X-ACCESS-CODE", cliente.getCodigo()))
                    .andExpect(status().isBadRequest());


            assertTrue(chamadoRepository.existsById(chamado.getId()));
        }

        @Test
        @DisplayName("Deve falhar quando um cliente tenta cancelar chamado de outro")
        void erroClienteSemAutorizacao() throws Exception {

            Cliente invasor = clienteRepository.save(Cliente.builder()
                    .nome("Invasor")
                    .codigo("999888")
                    .endereco("Rua Invasor")
                    .planoAtual(TipoPlano.BASICO)
                    .planoAgendado(TipoPlano.BASICO)
                    .build());

            driver.perform(delete(URI + "/" + chamado.getId())
                            .header("X-USER-TYPE", "CLIENTE")
                            .header("X-CLIENT-ID", invasor.getId())
                            .header("X-ACCESS-CODE", invasor.getCodigo()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve falhar se avançar para ATENDIMENTO sem informar técnico")
        void erroAvancarSemTecnico() throws Exception {
            chamado.setStatus(StatusChamado.AGUARDANDO_TECNICO);
            chamadoRepository.save(chamado);

            ChamadoPatchRequestDTO dto = new ChamadoPatchRequestDTO();
            dto.setStatusAcao("AVANCAR");
            dto.setTecnicoId(null);
            dto.setCodigo(empresa.getCodigoAcesso());

            driver.perform(patch(URI + "/" + chamado.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-TYPE", "EMPRESA")
                            .header("X-EMPRESA-CNPJ", empresa.getCnpj())
                            .header("X-ACCESS-CODE", empresa.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
