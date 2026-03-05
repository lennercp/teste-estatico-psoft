package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.HistoricoAssinatura;
import com.ufcg.psoft.commerce.model.TipoPlano;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.HistoricoAssinaturaRepository;
import com.ufcg.psoft.commerce.service.cliente.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import java.util.Collections;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private HistoricoAssinaturaRepository historicoAssinaturaRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = Cliente.builder()
                .id(1L)
                .nome("Nome Original")
                .endereco("Endereco Original")
                .codigo("123456")
                .planoAtual(TipoPlano.BASICO)
                .planoAgendado(TipoPlano.PREMIUM)
                .build();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de novo ciclo")
    class ClienteVerificacaoNovoCiclo {

        @Test
        @DisplayName("Quando novo ciclo cobranca plano agendado diferente do atual")
        void quandoNovoCicloCobrancaPlanoAlterado() {
            Long clienteId = 1L;
            cliente.setPlanoAtual(TipoPlano.BASICO);
            cliente.setPlanoAgendado(TipoPlano.PREMIUM);

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
            when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDTO.class)))
                    .thenReturn(ClienteResponseDTO.builder().build());

            clienteService.novoCicloCobranca(clienteId);

            assertEquals(TipoPlano.PREMIUM, cliente.getPlanoAtual());

            verify(historicoAssinaturaRepository, times(1)).save(any(HistoricoAssinatura.class));
        }

        @Test
        @DisplayName("Quando novo ciclo cobranca plano agendado igual ao atual")
        void quandoNovoCicloCobrancaPlanoNaoAlterado() {
            Long clienteId = 1L;
            cliente.setPlanoAtual(TipoPlano.BASICO);
            cliente.setPlanoAgendado(TipoPlano.BASICO);

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
            when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDTO.class)))
                    .thenReturn(ClienteResponseDTO.builder().build());

            clienteService.novoCicloCobranca(clienteId);

            assertEquals(TipoPlano.BASICO, cliente.getPlanoAtual());

            verify(historicoAssinaturaRepository, never()).save(any(HistoricoAssinatura.class));
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de listagem de serviços (US7)")
    class ClienteVerificacaoListarServicos {

        @Test
        @DisplayName("Deve buscar serviços com filtro de plano BASICO quando cliente é BASICO")
        void quandoClienteBasicoBuscaServicos() {
            cliente.setPlanoAtual(TipoPlano.BASICO);

            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(servicoRepository.buscarComFiltros(anyList(), any(), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            clienteService.listarServicosDisponiveis(cliente.getId(), cliente.getCodigo(), null, null, null, null,
                    null);

            List<TipoPlano> planosEsperados = List.of(TipoPlano.BASICO, TipoPlano.AMBOS);

            verify(servicoRepository).buscarComFiltros(
                    eq(planosEsperados),
                    any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve buscar todos os planos quando cliente é PREMIUM")
        void quandoClientePremiumBuscaServicos() {
            cliente.setPlanoAtual(TipoPlano.PREMIUM);

            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(servicoRepository.buscarComFiltros(anyList(), any(), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            clienteService.listarServicosDisponiveis(cliente.getId(), cliente.getCodigo(), null, null, null, null,
                    null);

            List<TipoPlano> planosEsperados = List.of(TipoPlano.BASICO, TipoPlano.PREMIUM, TipoPlano.AMBOS);

            verify(servicoRepository).buscarComFiltros(
                    eq(planosEsperados),
                    any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar erro se código de acesso for inválido")
        void quandoCodigoAcessoInvalido() {
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

            org.junit.jupiter.api.Assertions.assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
                clienteService.listarServicosDisponiveis(cliente.getId(), "CODIGO_ERRADO", null, null, null, null,
                        null);
            });

            verify(servicoRepository, never()).buscarComFiltros(any(), any(), any(), any(), any(), any());
        }
    }

}
