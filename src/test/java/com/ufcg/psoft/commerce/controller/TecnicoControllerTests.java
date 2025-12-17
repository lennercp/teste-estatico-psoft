package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do Controlador de Técnicos")
class TecnicoControllerTests {

    @Autowired
    private MockMvc driver;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TecnicoPostPutRequestDTO tecnicoDTO;

    @BeforeEach
    void setup() {
        // Limpa o banco antes de cada teste 
        tecnicoRepository.deleteAll();
        // Configura o ObjectMapper para lidar com datas se necessário
        objectMapper.registerModule(new JavaTimeModule());

        // Cria um DTO padrão para usar nos testes
        tecnicoDTO = TecnicoPostPutRequestDTO.builder()
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456") // 6 dígitos válido
                .build();
    }

    @AfterEach
    void tearDown() {
        tecnicoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de Criação de Técnico")
    class CriacaoTecnico {

        @Test
        @DisplayName("Deve criar técnico com dados válidos")
        void testCriarTecnicoSucesso() throws Exception {
            String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

            driver.perform(post("/api/v1/tecnicos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nomeCompleto").value(tecnicoDTO.getNomeCompleto()))
                    .andExpect(jsonPath("$.id").exists())
                    // Regra de segurança: Não deve retornar o código de acesso
                    .andExpect(jsonPath("$.codigoAcesso").doesNotExist())
                    .andDo(print());
        }

        @Test
        @DisplayName("Não deve criar técnico com código de acesso inválido (menos de 6 dígitos)")
        void testCriarTecnicoCodigoInvalido() throws Exception {
            tecnicoDTO.setCodigoAcesso("123"); // Inválido
            String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

            driver.perform(post("/api/v1/tecnicos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest()) // Espera erro 400
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Testes de Leitura de Técnico")
    class LeituraTecnico {

        @Test
        @DisplayName("Deve listar todos os técnicos sem exibir código de acesso")
        void testListarTecnicos() throws Exception {
            // Salva um técnico diretamente no banco
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Maria Souza")
                    .especialidade("Hidráulica")
                    .placaVeiculo("XYZ-9876")
                    .tipoVeiculo("Moto")
                    .corVeiculo("Vermelha")
                    .codigoAcesso("654321")
                    .build();
            tecnicoRepository.save(tecnico);

            driver.perform(get("/api/v1/tecnicos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nomeCompleto").value("Maria Souza"))
                    .andExpect(jsonPath("$[0].codigoAcesso").doesNotExist()) // Segurança
                    .andDo(print());
        }

        @Test
        @DisplayName("Deve recuperar técnico por ID")
        void testRecuperarTecnicoPorId() throws Exception {
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("João Teste")
                    .especialidade("Pintor")
                    .placaVeiculo("TEST-000")
                    .tipoVeiculo("Carro")
                    .corVeiculo("Azul")
                    .codigoAcesso("111222")
                    .build();
            Tecnico salvo = tecnicoRepository.save(tecnico);

            driver.perform(get("/api/v1/tecnicos/" + salvo.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nomeCompleto").value("João Teste"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Técnico")
    class AtualizacaoTecnico {

        @Test
        @DisplayName("Deve atualizar técnico quando código de acesso está correto")
        void testAtualizarTecnicoSucesso() throws Exception {
            // Cria o cenário inicial
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Antigo Nome")
                    .especialidade("Antiga")
                    .placaVeiculo("OLD-0000")
                    .tipoVeiculo("Carro")
                    .corVeiculo("Preto")
                    .codigoAcesso("123456")
                    .build();
            Tecnico salvo = tecnicoRepository.save(tecnico);

            // Modifica o DTO
            tecnicoDTO.setNomeCompleto("Novo Nome");

            String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

            // PUT passando o ID e o codigoAcesso correto como param
            driver.perform(put("/api/v1/tecnicos/" + salvo.getId())
                            .param("codigoAcesso", "123456")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nomeCompleto").value("Novo Nome"))
                    .andDo(print());
        }

        @Test
        @DisplayName("Deve falhar ao atualizar com código de acesso incorreto")
        void testAtualizarTecnicoSenhaErrada() throws Exception {
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Nome")
                    .especialidade("Esp")
                    .placaVeiculo("PLA-1234")
                    .tipoVeiculo("Carro")
                    .corVeiculo("Prata")
                    .codigoAcesso("123456")
                    .build();
            Tecnico salvo = tecnicoRepository.save(tecnico);

            String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

            // PUT com senha errada (000000)
            driver.perform(put("/api/v1/tecnicos/" + salvo.getId())
                            .param("codigoAcesso", "000000") 
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest()) // Ou o status que sua Exception Handler retorna
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Testes de Remoção de Técnico")
    class RemocaoTecnico {

        @Test
        @DisplayName("Deve remover técnico com código de acesso correto")
        void testRemoverTecnicoSucesso() throws Exception {
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Para Remover")
                    .especialidade("Nenhuma")
                    .placaVeiculo("DEL-9999")
                    .tipoVeiculo("Moto")
                    .corVeiculo("Verde")
                    .codigoAcesso("123456")
                    .build();
            Tecnico salvo = tecnicoRepository.save(tecnico);

            driver.perform(delete("/api/v1/tecnicos/" + salvo.getId())
                            .param("codigoAcesso", "123456"))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            // Verifica se realmente sumiu do banco
            assertTrue(tecnicoRepository.findById(salvo.getId()).isEmpty());
        }

        @Test
        @DisplayName("Deve falhar ao remover técnico com código de acesso incorreto")
        void testRemoverTecnicoSenhaErrada() throws Exception {
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Protegido")
                    .especialidade("Segurança")
                    .placaVeiculo("SEC-0000")
                    .tipoVeiculo("Carro")
                    .corVeiculo("Preto")
                    .codigoAcesso("123456")
                    .build();
            Tecnico salvo = tecnicoRepository.save(tecnico);

            driver.perform(delete("/api/v1/tecnicos/" + salvo.getId())
                            .param("codigoAcesso", "999999")) // Senha errada
                    .andExpect(status().isBadRequest())
                    .andDo(print());
            
            // Verifica que NÃO foi apagado
            assertTrue(tecnicoRepository.findById(salvo.getId()).isPresent());
        }
    }
}