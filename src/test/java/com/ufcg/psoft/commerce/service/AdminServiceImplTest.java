package com.ufcg.psoft.commerce.service;


import com.ufcg.psoft.commerce.dto.AdminPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AdminResponseDTO;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.repository.AdminRepository;
import com.ufcg.psoft.commerce.service.admin.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Service de Admin")
public class AdminServiceImplTest {


    @Mock
    AdminRepository adminRepository;

    @InjectMocks
    AdminServiceImpl adminService;

    static final String NOME = "Admin Teste";
    static final String SENHA = "123456";

    Admin admin;
    AdminPostPutRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        admin = Admin.builder()
                .nome(NOME)
                .senha(SENHA)
                .build();

        requestDTO = AdminPostPutRequestDTO.builder()
                .nome(NOME)
                .senha(SENHA)
                .build();
    }

    @Test
    @DisplayName("Quando criamos admin e não existe admin cadastrado")
    void quandoCriamosAdminSemAdminExistente() {

        when(adminRepository.count()).thenReturn(0L);
        when(adminRepository.save(any(Admin.class)))
                .thenReturn(admin);

        AdminResponseDTO resultado =
                adminService.criarAdmin(requestDTO);

        assertNotNull(resultado);
        verify(adminRepository).count();
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    @DisplayName("Quando tentamos criar admin e já existe admin")
    void quandoJaExisteAdmin() {

        when(adminRepository.count()).thenReturn(1L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.criarAdmin(requestDTO)
        );

        assertEquals(
                "Já existe um admin cadastrado no sistema",
                exception.getMessage()
        );

        verify(adminRepository).count();
        verify(adminRepository, never()).save(any());
    }

}