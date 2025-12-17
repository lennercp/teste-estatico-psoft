package com.ufcg.psoft.commerce.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Empresas")
public class EmpresaControllerTest {

    final String URI_EMPRESAS = "/empresas";

    @Autowired
    MockMvc driver;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    TecnicoRepository tecnicoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Empresa empresa;
    
    Tecnico tecnico;

    EmpresaPostPutRequestDTO empresaPostPutRequestDTO;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());

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

        tecnico = tecnicoRepository.save(Tecnico.builder()
                .nomeCompleto("Tecnico Teste")
                .especialidade("Geral")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        tecnicoRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do nome")
    class EmpresaVerificacaoNome {

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
        @DisplayName("Quando alteramos o nome da empresa com dados válidos")
        void quandoAlteramosNomeEmpresaValido() throws Exception {

            empresaPostPutRequestDTO.setNomeFantasia("Empresa Alterada SA");

            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", empresa.getCodigoAcesso())
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EmpresaResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, EmpresaResponseDTO.class);

            assertEquals("Empresa Alterada SA", resultado.getNomeFantasia());
        }


        @Test
        @DisplayName("Quando alteramos o nome da empresa nulo")
        void quandoAlteramosNomeEmpresaNulo() throws Exception {

            empresaPostPutRequestDTO.setNomeFantasia(null);

            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", empresa.getCodigoAcesso())
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
        @DisplayName("Quando criamos uma empresa válida")
        void quandoCriamosEmpresaValida() throws Exception {

            empresaRepository.deleteAll();

            String responseJsonString = driver.perform(post(URI_EMPRESAS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(empresaPostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            EmpresaResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, EmpresaResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getCnpj()),
                    () -> assertEquals(empresaPostPutRequestDTO.getNomeFantasia(), resultado.getNomeFantasia())
            );
        }

        @Test
        @DisplayName("Quando excluímos uma empresa válida")
        void quandoExcluimosEmpresaValida() throws Exception {

            String responseJsonString = driver.perform(delete(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando excluímos empresa com código inválido")
        void quandoExcluimosEmpresaCodigoInvalido() throws Exception {

            String responseJsonString = driver.perform(delete(URI_EMPRESAS + "/" + empresa.getCnpj())
                            .param("codigo", "000000"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado =
                    objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

    }


    @Nested
    @DisplayName("Conjunto de casos de verificação de aprovação e reprovação de técnicos")
    class EmpresaVerificacaoAprovacaoReprovacaoTecnico {

        @Test
        @DisplayName("Quando aprovamos tecnico valido")
        void quandoAprovamosTecnicoValido() throws Exception {
            driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj() + "/aprovar/" + tecnico.getId())
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        @DisplayName("Quando aprovamos tecnico com empresa inexistente")
        void quandoAprovamosTecnicoEmpresaInexistente() throws Exception {
            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/00000000000000/aprovar/" + tecnico.getId())
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("A empresa consultada não existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando aprovamos tecnico inexistente")
        void quandoAprovamosTecnicoInexistente() throws Exception {
            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj() + "/aprovar/" + 99999L)
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O técnico consultado não existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando rejeitamos tecnico valido")
        void quandoRejeitamosTecnicoValido() throws Exception {
            driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj() + "/rejeitar/" + tecnico.getId())
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        @DisplayName("Quando rejeitamos tecnico com empresa inexistente")
        void quandoRejeitamosTecnicoEmpresaInexistente() throws Exception {
            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/00000000000000/rejeitar/" + tecnico.getId())
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("A empresa consultada não existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando rejeitamos tecnico inexistente")
        void quandoRejeitamosTecnicoInexistente() throws Exception {
            String responseJsonString = driver.perform(put(URI_EMPRESAS + "/" + empresa.getCnpj() + "/rejeitar/" + 99999L)
                            .param("codigo", empresa.getCodigoAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O técnico consultado não existe!", resultado.getMessage());
        }

    }

}

