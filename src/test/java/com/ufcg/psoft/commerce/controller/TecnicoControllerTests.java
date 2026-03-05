package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
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

    static final String URI_TECNICOS = "/tecnicos";

    @Autowired
    private MockMvc driver;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TecnicoPostPutRequestDTO tecnicoDTO;

    @BeforeEach
    void setup() {
        tecnicoRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());

        tecnicoDTO = TecnicoPostPutRequestDTO.builder()
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
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

            driver.perform(post(URI_TECNICOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nomeCompleto").value(tecnicoDTO.getNomeCompleto()))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.codigoAcesso").doesNotExist())
                    .andDo(print());
        }

        @Test
        @DisplayName("Não deve criar técnico com código de acesso inválido (menos de 6 dígitos)")
        void testCriarTecnicoCodigoInvalido() throws Exception {
            tecnicoDTO.setCodigoAcesso("123");
            String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

            driver.perform(post(URI_TECNICOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Testes de Leitura de Técnico")
    class LeituraTecnico {

        @Test
        @DisplayName("Deve listar todos os técnicos sem exibir código de acesso")
        void testListarTecnicos() throws Exception {
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Maria Souza")
                    .especialidade("Hidráulica")
                    .placaVeiculo("XYZ-9876")
                    .tipoVeiculo("Moto")
                    .corVeiculo("Vermelha")
                    .codigoAcesso("654321")
                    .build();
            tecnicoRepository.save(tecnico);

            driver.perform(get(URI_TECNICOS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nomeCompleto").value("Maria Souza"))
                    .andExpect(jsonPath("$[0].codigoAcesso").doesNotExist())
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

            driver.perform(get(URI_TECNICOS + "/" +  salvo.getId()))
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
            Tecnico tecnico = Tecnico.builder()
                    .nomeCompleto("Antigo Nome")
                    .especialidade("Antiga")
                    .placaVeiculo("OLD-0000")
                    .tipoVeiculo("Carro")
                    .corVeiculo("Preto")
                    .codigoAcesso("123456")
                    .build();
            Tecnico salvo = tecnicoRepository.save(tecnico);

            tecnicoDTO.setNomeCompleto("Novo Nome");

            String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

            driver.perform(put(URI_TECNICOS + "/" + salvo.getId())
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

            driver.perform(put(URI_TECNICOS + "/" + salvo.getId())
                            .param("codigoAcesso", "000000") 
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
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

            driver.perform(delete(URI_TECNICOS + "/" + salvo.getId())
                            .param("codigoAcesso", "123456"))
                    .andExpect(status().isNoContent())
                    .andDo(print());

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

            driver.perform(delete(URI_TECNICOS + "/" + salvo.getId())
                            .param("codigoAcesso", "999999"))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
            
            assertTrue(tecnicoRepository.findById(salvo.getId()).isPresent());
        }
    }

    @Nested
    @DisplayName("Testes de Disponibilidade do Técnico")
    class DisponibilidadeTecnico {

        @Test
        @DisplayName("Deve alterar disponibilidade para ATIVO")
        void testAlterarDisponibilidadeSucesso() throws Exception {

            Tecnico tecnico = tecnicoRepository.save(
                    Tecnico.builder()
                            .nomeCompleto("Teste")
                            .especialidade("Eletricista")
                            .placaVeiculo("AAA-1111")
                            .tipoVeiculo("Carro")
                            .corVeiculo("Branco")
                            .codigoAcesso("123456")
                            .build()
            );

            tecnicoDTO.setDisponibilidade(DisponibilidadeStatus.ATIVO);

            String json =
                    objectMapper.writeValueAsString(tecnicoDTO);

            driver.perform(
                            patch(URI_TECNICOS + "/" +
                                    tecnico.getId() +
                                    "/disponibilidade")
                                    .param("codigoAcesso", "123456")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.disponibilidade")
                                    .value("ATIVO")
                    )
                    .andDo(print());
        }

        @Test
        @DisplayName("Não deve alterar disponibilidade com código inválido")
        void testAlterarDisponibilidadeCodigoInvalido() throws Exception {

            Tecnico tecnico = tecnicoRepository.save(
                    Tecnico.builder()
                            .nomeCompleto("Teste")
                            .especialidade("Eletricista")
                            .placaVeiculo("AAA-1111")
                            .tipoVeiculo("Carro")
                            .corVeiculo("Branco")
                            .codigoAcesso("123456")
                            .build()
            );

            tecnicoDTO.setDisponibilidade(DisponibilidadeStatus.ATIVO);

            String json =
                    objectMapper.writeValueAsString(tecnicoDTO);

            driver.perform(
                            patch(URI_TECNICOS + "/" +
                                    tecnico.getId() +
                                    "/disponibilidade")
                                    .param("codigoAcesso", "000000")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("Deve retornar data da última atualização da disponibilidade")
        void testDisponibilidadeAtualizadaEm() throws Exception {

            Tecnico tecnico = tecnicoRepository.save(
                    Tecnico.builder()
                            .nomeCompleto("Teste")
                            .especialidade("Eletricista")
                            .placaVeiculo("AAA-1111")
                            .tipoVeiculo("Carro")
                            .corVeiculo("Branco")
                            .codigoAcesso("123456")
                            .build()
            );

            driver.perform(get(URI_TECNICOS + "/" + tecnico.getId()))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.disponibilidadeAtualizadaEm")
                                    .exists()
                    );
        }


    }
}