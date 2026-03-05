
package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.ServicoInteresseRequestDTO;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.ServicoDisponivelException;
import com.ufcg.psoft.commerce.exception.ServicoNaoExisteException;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Servico;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import com.ufcg.psoft.commerce.service.interesse.InteresseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Service de Interesse")
class InteresseServiceTest {

    @Mock
    InteresseRepository interesseRepository;

    @Mock
    ServicoRepository servicoRepository;

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    InteresseServiceImpl interesseService;

    static final String CNPJ = "12345678910111";
    static final String CODIGO_ACESSO_EMPRESA = "123456";
    static final Long SERVICO_ID = 1L;
    static final Long CLIENTE_ID = 1L;
    static final String CODIGO_ACESSO_CLIENTE = "654321";

    Servico servico;
    Cliente cliente;
    ServicoInteresseRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        servico = Servico.builder()
                .id(SERVICO_ID)
                .nome("Serviço Teste")
                .ativo(false) // Serviço indisponível para poder demonstrar interesse
                .build();

        cliente = Cliente.builder()
                .id(CLIENTE_ID)
                .nome("Cliente Teste")
                .build();

        requestDTO = ServicoInteresseRequestDTO.builder()
                .id(CLIENTE_ID)
                .codigoAcesso(CODIGO_ACESSO_CLIENTE)
                .build();
    }

    @Test
    @DisplayName("Quando adicionamos interesse em serviço indisponível (sucesso)")
    void adicionarInteresseSucesso() {
        doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_ACESSO_EMPRESA);
        doNothing().when(authService).autenticarCliente(CLIENTE_ID, CODIGO_ACESSO_CLIENTE);

        when(servicoRepository.findById(SERVICO_ID)).thenReturn(Optional.of(servico));
        when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(cliente));

        interesseService.adicionarInteresse(CNPJ, CODIGO_ACESSO_EMPRESA, SERVICO_ID, requestDTO);

        verify(interesseRepository).save(any(Interesse.class));
    }

    @Test
    @DisplayName("Quando tentamos adicionar interesse em serviço disponível")
    void adicionarInteresseServicoDisponivel() {
        servico.setAtivo(true); // Serviço disponível

        doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_ACESSO_EMPRESA);
        doNothing().when(authService).autenticarCliente(CLIENTE_ID, CODIGO_ACESSO_CLIENTE);

        when(servicoRepository.findById(SERVICO_ID)).thenReturn(Optional.of(servico));

        assertThrows(ServicoDisponivelException.class, () ->
                interesseService.adicionarInteresse(CNPJ, CODIGO_ACESSO_EMPRESA, SERVICO_ID, requestDTO));

        verify(interesseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Quando tentamos adicionar interesse em serviço inexistente")
    void adicionarInteresseServicoNaoExiste() {
        doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_ACESSO_EMPRESA);
        doNothing().when(authService).autenticarCliente(CLIENTE_ID, CODIGO_ACESSO_CLIENTE);

        when(servicoRepository.findById(SERVICO_ID)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoExisteException.class, () ->
                interesseService.adicionarInteresse(CNPJ, CODIGO_ACESSO_EMPRESA, SERVICO_ID, requestDTO));

        verify(interesseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Quando tentamos adicionar interesse com cliente inexistente")
    void adicionarInteresseClienteNaoExiste() {
        doNothing().when(authService).autenticarEmpresa(CNPJ, CODIGO_ACESSO_EMPRESA);
        doNothing().when(authService).autenticarCliente(CLIENTE_ID, CODIGO_ACESSO_CLIENTE);

        when(servicoRepository.findById(SERVICO_ID)).thenReturn(Optional.of(servico));
        when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoExisteException.class, () ->
                interesseService.adicionarInteresse(CNPJ, CODIGO_ACESSO_EMPRESA, SERVICO_ID, requestDTO));

        verify(interesseRepository, never()).save(any());
    }
}
