package com.ufcg.psoft.commerce.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.repository.AdminRepository;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Empresas")
public class EmpresaControllerTest {

    static final Long ADMIN_ID = 1L;
    final String SENHA_ADMIN_VALIDA = "admin123";
    final String SENHA_ADMIN_INVALIDA = "adminErrada";

    final String URI_EMPRESAS = "/empresas";

    @Autowired
    MockMvc driver;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    AdminRepository adminRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Empresa empresa;

    EmpresaPostPutRequestDTO empresaPostPutRequestDTO;

    @BeforeEach
    void setup() {


        objectMapper.registerModule(new JavaTimeModule());

        adminRepository.save(
                Admin.builder()
                        .id(ADMIN_ID)
                        .nome("admin")
                        .senha(SENHA_ADMIN_VALIDA)
                        .build()
        );

        empresa = empresaRepository.save(Empresa.builder()
                .cnpj("12345678910111")
                .nomeFantasia("Empresa Teste LTDA")
                .endereco("Rua das Empresas, 100")
                .codigoAcesso("654321")
                .build()
        );

        empresaPostPutRequestDTO = EmpresaPostPutRequestDTO.builder()
                .cnpj(empresa.getCnpj())
                .nomeFantasia(empresa.getNomeFantasia())
                .endereco(empresa.getEndereco())
                .codigoAcesso(empresa.getCodigoAcesso())
                .build();
    }

    @AfterEach
    void tearDown() {
        empresaRepository.deleteAll();  adminRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do nome")
    class EmpresaVerificacao {
        @Test
        @DisplayName("Quando alteramos o nome da empresa com admin e código válidos")
        void quandoAlteramosNomeEmpresaAdminECodigoValidos() throws Exception {

            empresaPostPutRequestDTO.setNomeFantasia("Empresa Alterada SA");

            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", empresa.getCodigoAcesso())
                            .param("senhaAdmin", SENHA_ADMIN_VALIDA)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EmpresaResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, EmpresaResponseDTO.class);

            assertEquals("Empresa Alterada SA", resultado.getNomeFantasia());
        }

        @Test
        @DisplayName("Quando alteramos empresa com admin válido e código inválido")
        void quandoAlteramosEmpresaCodigoInvalidoAdminValido() throws Exception {

            driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", "000000")
                            .param("senhaAdmin", SENHA_ADMIN_VALIDA)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando alteramos empresa com admin inválido e código válido")
        void quandoAlteramosEmpresaAdminInvalidoCodigoValido() throws Exception {

            driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", empresa.getCodigoAcesso())
                            .param("senhaAdmin", SENHA_ADMIN_INVALIDA)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando alteramos empresa com admin e código inválidos")
        void quandoAlteramosEmpresaAdminECodigoInvalidos() throws Exception {

            driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigo", "000000")
                            .param("senhaAdmin", SENHA_ADMIN_INVALIDA)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isBadRequest());
        }





        @Test
        @DisplayName("Quando recuperamos uma empresa com dados válidos")
        void quandoRecuperamosNomeEmpresaValida() throws Exception {

            String responseJsonString = driver.perform(get(URI_EMPRESAS + "/" + empresa.getCnpj()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EmpresaResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, EmpresaResponseDTO.class);


            assertEquals("Empresa Teste LTDA", resultado.getNomeFantasia());
        }


        @Test
        @DisplayName("Quando alteramos o nome da empresa nulo com admin e código válidos")
        void quandoAlteramosNomeEmpresaNuloAdminValido() throws Exception {

            empresaPostPutRequestDTO.setNomeFantasia(null);

            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", empresa.getCodigoAcesso())
                            .param("senhaAdmin", SENHA_ADMIN_VALIDA)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            CustomErrorType resultado =
                    objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome fantasia é obrigatório", resultado.getErrors().get(0))
            );
        }


    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class EmpresaVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando buscamos todas empresas salvas")
        void quandoBuscamosTodasEmpresas() throws Exception {

            Empresa empresa2 = Empresa.builder()
                    .cnpj("98765432000188")
                    .nomeFantasia("Outra Empresa")
                    .endereco("Av. Comercial, 200")
                    .codigoAcesso("112233")
                    .build();

            empresaRepository.save(empresa2);

            String responseJsonString = driver.perform(get(URI_EMPRESAS))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<EmpresaResponseDTO> resultado =
                    objectMapper.readValue(responseJsonString, new TypeReference<>() {});

            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Quando buscamos uma empresa inexistente")
        void quandoBuscamosEmpresaInexistente() throws Exception {

            String responseJsonString = driver.perform(get(URI_EMPRESAS + "/00000000000000"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            CustomErrorType resultado =
                    objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("A empresa consultada não existe!", resultado.getMessage());
        }


        @Test
        @DisplayName("Quando criamos empresa com admin válido")
        void quandoCriamosEmpresaAdminValido() throws Exception {

            empresaRepository.deleteAll();

            driver.perform(post(URI_EMPRESAS)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("senhaAdmin", SENHA_ADMIN_VALIDA)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Quando criamos empresa com admin inválido")
        void quandoCriamosEmpresaAdminInvalido() throws Exception {

            driver.perform(post(URI_EMPRESAS)
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("senhaAdmin", SENHA_ADMIN_INVALIDA)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("Quando excluímos empresa com admin e código válidos")
        void quandoExcluimosEmpresaAdminECodigoValidos() throws Exception {

            driver.perform(delete(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", empresa.getCodigoAcesso())
                            .param("senhaAdmin", SENHA_ADMIN_VALIDA))
                    .andExpect(status().isNoContent());

        }



        @Test
        @DisplayName("Quando excluímos empresa com admin válido e código inválido")
        void quandoExcluimosEmpresaAdminValidoCodigoInvalido() throws Exception {

            driver.perform(delete(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", "000000")
                            .param("senhaAdmin", SENHA_ADMIN_VALIDA))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando excluímos empresa com admin inválido")
        void quandoExcluimosEmpresaAdminInvalido() throws Exception {

            driver.perform(delete(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .param("id", String.valueOf(ADMIN_ID))
                            .param("codigoAcesso", empresa.getCodigoAcesso())
                            .param("senhaAdmin", SENHA_ADMIN_INVALIDA))
                    .andExpect(status().isBadRequest());
        }

    }

}
