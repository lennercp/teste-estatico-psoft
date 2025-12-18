package com.ufcg.psoft.commerce.service;


import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.exception.AdminSenhaInvalidaException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import com.ufcg.psoft.commerce.service.empresa.EmpresaServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Service de Empresa")
public class EmpresaServiceImplTest {

    @Mock
    EmpresaRepository empresaRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    AuthService authService;

    @InjectMocks
    EmpresaServiceImpl empresaService;

    static final String CNPJ = "12345678910111";
    static final String CODIGO = "654321";
    static final Long ADMIN_ID = 1L;
    static final String SENHA_ADMIN = "senhaAdminValida";

    Empresa empresa;
    EmpresaPostPutRequestDTO requestDTO;
    EmpresaResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        empresa = Empresa.builder()
                .cnpj(CNPJ)
                .nomeFantasia("Empresa Teste")
                .endereco("Rua A Bairro B Cidade C")
                .codigoAcesso(CODIGO)
                .build();

        requestDTO = EmpresaPostPutRequestDTO.builder()
                .cnpj(CNPJ)
                .nomeFantasia("Empresa Alterada")
                .endereco("Rua B Bairro C Cidade D")
                .codigoAcesso(CODIGO)
                .build();

        responseDTO = new EmpresaResponseDTO(empresa);
    }



    @Test
    @DisplayName("Quando criamos empresa válida")
    void quandoCriamosEmpresaValida() {

        doNothing().when(authService)
                .autenticarAdmin(ADMIN_ID, SENHA_ADMIN);

        when(modelMapper.map(requestDTO, Empresa.class))
                .thenReturn(empresa);

        when(modelMapper.map(empresa, EmpresaResponseDTO.class))
                .thenReturn(responseDTO);

        EmpresaResponseDTO resultado =
                empresaService.criar(ADMIN_ID, SENHA_ADMIN, requestDTO);

        assertNotNull(resultado);

        verify(authService).autenticarAdmin(ADMIN_ID, SENHA_ADMIN);
        verify(empresaRepository).save(empresa);
    }

    @Test
    @DisplayName("Quando tentamos criar empresa com senha de admin inválida")
    void quandoCriamosEmpresaComSenhaAdminInvalida() {

        doThrow(AdminSenhaInvalidaException.class)
                .when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        assertThrows(AdminSenhaInvalidaException.class,
                () -> empresaService.criar(ADMIN_ID,SENHA_ADMIN, requestDTO));

        verify(empresaRepository, never()).save(any());
    }



    @Test
    @DisplayName("Quando alteramos empresa válida")
    void quandoAlteramosEmpresaValida() {

        doNothing().when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        doNothing().when(authService)
                .autenticarEmpresa(CNPJ, CODIGO);

        when(empresaRepository.findByCnpj(CNPJ))
                .thenReturn(Optional.of(empresa));

        doAnswer(invocation -> {
            EmpresaPostPutRequestDTO dto = invocation.getArgument(0);
            Empresa ent = invocation.getArgument(1);
            ent.setNomeFantasia(dto.getNomeFantasia());
            ent.setEndereco(dto.getEndereco());
            ent.setCodigoAcesso(dto.getCodigoAcesso());
            return null;
        }).when(modelMapper)
                .map(any(EmpresaPostPutRequestDTO.class), any(Empresa.class));

        when(modelMapper.map(any(Empresa.class), eq(EmpresaResponseDTO.class)))
                .thenReturn(responseDTO);

        EmpresaResponseDTO resultado =
                empresaService.alterar(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN, requestDTO);

        assertNotNull(resultado);

        verify(authService).autenticarAdmin(ADMIN_ID,SENHA_ADMIN);
        verify(authService).autenticarEmpresa(CNPJ, CODIGO);
        verify(empresaRepository).save(empresa);
    }

    @Test
    @DisplayName("Quando tentamos alterar empresa com senha de admin inválida")
    void quandoAlteramosEmpresaComSenhaAdminInvalida() {

        doThrow(AdminSenhaInvalidaException.class)
                .when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        assertThrows(AdminSenhaInvalidaException.class,
                () -> empresaService.alterar(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN, requestDTO));

        verify(empresaRepository, never()).save(any());
    }


    @Test
    @DisplayName("Quando removemos empresa válida")
    void quandoRemovemosEmpresaValida() {

        doNothing().when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        doNothing().when(authService)
                .autenticarEmpresa(CNPJ, CODIGO);

        when(empresaRepository.findByCnpj(CNPJ))
                .thenReturn(Optional.of(empresa));

        empresaService.remover(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN);

        verify(authService).autenticarAdmin(ADMIN_ID,SENHA_ADMIN);
        verify(authService).autenticarEmpresa(CNPJ, CODIGO);
        verify(empresaRepository).delete(empresa);
    }

    @Test
    @DisplayName("Quando tentamos remover empresa com senha de admin inválida")
    void quandoRemovemosEmpresaComSenhaAdminInvalida() {

        doThrow(AdminSenhaInvalidaException.class)
                .when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        assertThrows(AdminSenhaInvalidaException.class,
                () -> empresaService.remover(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN));

        verify(empresaRepository, never()).delete(any());
    }



    @Test
    @DisplayName("Quando listamos empresas")
    void quandoListamosEmpresas() {

        when(empresaRepository.findAll())
                .thenReturn(List.of(empresa));

        List<EmpresaResponseDTO> resultado = empresaService.listar();

        assertFalse(resultado.isEmpty());
    }

    @Test
    @DisplayName("Quando recuperamos empresa válida")
    void quandoRecuperamosEmpresaValida() {

        when(empresaRepository.findByCnpj(CNPJ))
                .thenReturn(Optional.of(empresa));

        EmpresaResponseDTO resultado =
                empresaService.recuperar(CNPJ);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Quando tentamos alterar empresa inexistente com credenciais válidas")
    void quandoAlteramosEmpresaInexistenteComCredenciaisValidas() {

        doNothing().when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        doNothing().when(authService)
                .autenticarEmpresa(CNPJ, CODIGO);

        when(empresaRepository.findByCnpj(CNPJ))
                .thenReturn(Optional.empty());

        assertThrows(EmpresaNaoExisteException.class,
                () -> empresaService.alterar(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN, requestDTO));

        verify(empresaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Quando tentamos remover empresa inexistente com credenciais válidas")
    void quandoRemovemosEmpresaInexistenteComCredenciaisValidas() {

        doNothing().when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        doNothing().when(authService)
                .autenticarEmpresa(CNPJ, CODIGO);

        when(empresaRepository.findByCnpj(CNPJ))
                .thenReturn(Optional.empty());

        assertThrows(EmpresaNaoExisteException.class,
                () -> empresaService.remover(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN));

        verify(empresaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Quando tentamos recuperar empresa inexistente")
    void quandoRecuperamosEmpresaInexistente() {

        when(empresaRepository.findByCnpj(CNPJ))
                .thenReturn(Optional.empty());

        assertThrows(EmpresaNaoExisteException.class,
                () -> empresaService.recuperar(CNPJ));
    }

    @Test
    @DisplayName("Quando senha do admin é inválida, não deve buscar empresa")
    void quandoSenhaAdminInvalidaNaoBuscaEmpresa() {

        doThrow(AdminSenhaInvalidaException.class)
                .when(authService)
                .autenticarAdmin(ADMIN_ID,SENHA_ADMIN);

        assertThrows(AdminSenhaInvalidaException.class,
                () -> empresaService.alterar(ADMIN_ID,CNPJ, CODIGO, SENHA_ADMIN, requestDTO));

        verify(empresaRepository, never()).findByCnpj(any());
    }

}