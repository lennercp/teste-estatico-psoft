package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
import com.ufcg.psoft.commerce.repository.ChamadoRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.service.atribuicao.AtribuicaoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AtribuicaoServiceImpl")
class AtribuicaoServiceImplTest {

    @InjectMocks
    private AtribuicaoServiceImpl service;

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private TecnicoRepository tecnicoRepository;

    private Chamado chamado;
    private Tecnico tecnico;

    @BeforeEach
    void setup() {
        tecnico = Tecnico.builder()
                .id(1L)
                .nomeCompleto("Tecnico Teste")
                .disponibilidade(DisponibilidadeStatus.ATIVO)
                .disponibilidadeAtualizadaEm(LocalDateTime.now().minusHours(1))
                .build();

        chamado = Chamado.builder()
                .id(10L)
                .status(StatusChamado.AGUARDANDO_TECNICO)
                .build();
    }

    // =========================================================================
    // processarChamadoEmAndamento
    // =========================================================================
    @Nested
    @DisplayName("processarChamadoEmAndamento")
    class ProcessarChamadoEmAndamento {

        @Test
        @DisplayName("Deve atribuir técnico ATIVO ao chamado e mudar disponibilidade para DESCANSO")
        void comTecnicoDisponivel() {
            when(tecnicoRepository.findFirstByDisponibilidadeOrderByDisponibilidadeAtualizadaEmAsc(
                    DisponibilidadeStatus.ATIVO))
                    .thenReturn(Optional.of(tecnico));

            service.processarChamadoEmAndamento(chamado);

            // O técnico foi atribuído ao chamado
            assertEquals(tecnico, chamado.getTecnico());
            // O status avançou de AGUARDANDO_TECNICO para ATENDIMENTO
            assertEquals(StatusChamado.ATENDIMENTO, chamado.getStatus());
            // A disponibilidade do técnico mudou para DESCANSO
            assertEquals(DisponibilidadeStatus.DESCANSO, tecnico.getDisponibilidade());
            verify(tecnicoRepository).findFirstByDisponibilidadeOrderByDisponibilidadeAtualizadaEmAsc(
                    DisponibilidadeStatus.ATIVO);
        }

        @Test
        @DisplayName("Não deve alterar o chamado quando não há técnico disponível (Optional vazio)")
        void semTecnicoDisponivel() {
            when(tecnicoRepository.findFirstByDisponibilidadeOrderByDisponibilidadeAtualizadaEmAsc(
                    DisponibilidadeStatus.ATIVO))
                    .thenReturn(Optional.empty());

            // Guarda o status original para comparar depois
            StatusChamado statusAntes = chamado.getStatus();

            service.processarChamadoEmAndamento(chamado);

            // Nada deve ter mudado no chamado
            assertNull(chamado.getTecnico());
            assertEquals(statusAntes, chamado.getStatus());
            verify(tecnicoRepository).findFirstByDisponibilidadeOrderByDisponibilidadeAtualizadaEmAsc(
                    DisponibilidadeStatus.ATIVO);
        }
    }

    // =========================================================================
    // processarTecnicoAtivo
    // =========================================================================
    @Nested
    @DisplayName("processarTecnicoAtivo")
    class ProcessarTecnicoAtivo {

        @Test
        @DisplayName("Deve atribuir o chamado mais antigo ao técnico e mudar disponibilidade para DESCANSO")
        void comChamadoAguardando() {
            when(chamadoRepository.findFirstByStatusOrderByDataAtualizacaoAsc(
                    StatusChamado.AGUARDANDO_TECNICO))
                    .thenReturn(Optional.of(chamado));

            service.processarTecnicoAtivo(tecnico);

            // O técnico foi atribuído ao chamado
            assertEquals(tecnico, chamado.getTecnico());
            // O status avançou de AGUARDANDO_TECNICO para ATENDIMENTO
            assertEquals(StatusChamado.ATENDIMENTO, chamado.getStatus());
            // A disponibilidade do técnico mudou para DESCANSO
            assertEquals(DisponibilidadeStatus.DESCANSO, tecnico.getDisponibilidade());
            verify(chamadoRepository).findFirstByStatusOrderByDataAtualizacaoAsc(
                    StatusChamado.AGUARDANDO_TECNICO);
        }

        @Test
        @DisplayName("Não deve alterar nada quando não há chamado aguardando técnico (Optional vazio)")
        void semChamadoAguardando() {
            when(chamadoRepository.findFirstByStatusOrderByDataAtualizacaoAsc(
                    StatusChamado.AGUARDANDO_TECNICO))
                    .thenReturn(Optional.empty());

            DisponibilidadeStatus disponibilidadeAntes = tecnico.getDisponibilidade();

            service.processarTecnicoAtivo(tecnico);

            // A disponibilidade do técnico não mudou
            assertEquals(disponibilidadeAntes, tecnico.getDisponibilidade());
            verify(chamadoRepository).findFirstByStatusOrderByDataAtualizacaoAsc(
                    StatusChamado.AGUARDANDO_TECNICO);
        }
    }
}
