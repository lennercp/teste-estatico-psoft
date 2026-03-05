package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.AdminPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AdminResponseDTO;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.repository.AdminRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest

@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Admin")
class AdminControllerTest {

    static final String URL_ADMINS = "/admin";

    @Autowired
    MockMvc driver;

    @Autowired
    AdminRepository adminRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Admin admin;
    AdminPostPutRequestDTO adminPostPutRequestDTO;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());

        adminPostPutRequestDTO = AdminPostPutRequestDTO.builder()
                .nome("Admin Teste")
                .senha("123456")
                .build();
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de criação de Admin")
    class CriacaoAdmin {

        @Test
        @DisplayName("Quando criamos admin com dados válidos")
        void quandoCriamosAdminValido() throws Exception {

            String responseJsonString = driver.perform(
                    post(URL_ADMINS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminPostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            AdminResponseDTO resultado = objectMapper.readValue(responseJsonString, AdminResponseDTO.class);

            assertEquals("Admin Teste", resultado.getNome());
        }

        @Test
        @DisplayName("Quando tentamos criar admin e já existe um cadastrado")
        void quandoJaExisteAdmin() throws Exception {

            adminRepository.save(Admin.builder()
                    .nome("Admin Existente")
                    .senha("123456")
                    .build());

            driver.perform(
                    post(URL_ADMINS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
