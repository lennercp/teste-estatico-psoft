package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;
import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Empresa;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Chamado chamado;
    private Tecnico tecnico;

    @BeforeEach
    void setup() {
        Empresa empresa = Empresa.builder()
                .cnpj("12.345.678/0001-99")
                .nomeFantasia("Empresa Teste")
                .endereco("Rua Teste, 1")
                .codigoAcesso("111111")
                .build();

        Set<Empresa> empresasAprovadoras = new HashSet<>();
        empresasAprovadoras.add(empresa);

        tecnico = Tecnico.builder()
                .id(1L)
                .nomeCompleto("Tecnico Teste")
                .disponibilidade(DisponibilidadeStatus.ATIVO)
                .disponibilidadeAtualizadaEm(LocalDateTime.now().minusHours(1))
                .empresasAprovadoras(empresasAprovadoras)
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
        @DisplayName("Deve atribuir técnico ATIVO aprovado ao chamado, mudar disponibilidade para DESCANSO e publicar evento")
        void comTecnicoDisponivel() {
            when(tecnicoRepository
                    .findFirstByDisponibilidadeAndEmpresasAprovadorasIsNotEmptyOrderByDisponibilidadeAtualizadaEmAsc(
                            DisponibilidadeStatus.ATIVO))
                    .thenReturn(Optional.of(tecnico));

            service.processarChamadoEmAndamento(chamado);

            // O técnico foi atribuído ao chamado
            assertEquals(tecnico, chamado.getTecnico());
            // O status avançou de AGUARDANDO_TECNICO para ATENDIMENTO
            assertEquals(StatusChamado.ATENDIMENTO, chamado.getStatus());
            // A disponibilidade do técnico mudou para DESCANSO
            assertEquals(DisponibilidadeStatus.DESCANSO, tecnico.getDisponibilidade());

            // O evento foi publicado com o chamado correto
            ArgumentCaptor<ChamadoEmAtendimentoEvent> eventCaptor = ArgumentCaptor
                    .forClass(ChamadoEmAtendimentoEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertSame(chamado, eventCaptor.getValue().getChamado());

            verify(tecnicoRepository)
                    .findFirstByDisponibilidadeAndEmpresasAprovadorasIsNotEmptyOrderByDisponibilidadeAtualizadaEmAsc(
                            DisponibilidadeStatus.ATIVO);
        }

        @Test
        @DisplayName("Não deve alterar o chamado nem publicar evento quando não há técnico aprovado disponível")
        void semTecnicoDisponivel() {
            when(tecnicoRepository
                    .findFirstByDisponibilidadeAndEmpresasAprovadorasIsNotEmptyOrderByDisponibilidadeAtualizadaEmAsc(
                            DisponibilidadeStatus.ATIVO))
                    .thenReturn(Optional.empty());

            StatusChamado statusAntes = chamado.getStatus();

            service.processarChamadoEmAndamento(chamado);

            // Nada deve ter mudado no chamado
            assertNull(chamado.getTecnico());
            assertEquals(statusAntes, chamado.getStatus());

            // Nenhum evento deve ter sido publicado
            verify(eventPublisher, never()).publishEvent(any());

            verify(tecnicoRepository)
                    .findFirstByDisponibilidadeAndEmpresasAprovadorasIsNotEmptyOrderByDisponibilidadeAtualizadaEmAsc(
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
        @DisplayName("Deve atribuir o chamado mais antigo ao técnico aprovado e mudar disponibilidade para DESCANSO")
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

        @Test
        @DisplayName("Deve ignorar técnico sem empresa aprovadora sem consultar repositório de chamados")
        void tecnicoSemEmpresaAprovadora() {
            // Técnico sem nenhuma empresa aprovadora
            Tecnico tecnicoSemAprovacao = Tecnico.builder()
                    .id(2L)
                    .nomeCompleto("Sem Aprovação")
                    .disponibilidade(DisponibilidadeStatus.ATIVO)
                    .disponibilidadeAtualizadaEm(LocalDateTime.now())
                    .empresasAprovadoras(new HashSet<>())
                    .build();

            service.processarTecnicoAtivo(tecnicoSemAprovacao);

            // Não deve ter consultado o repositório de chamados
            verify(chamadoRepository, never()).findFirstByStatusOrderByDataAtualizacaoAsc(any());
            // O técnico permanece sem atribuição e com disponibilidade original
            assertEquals(DisponibilidadeStatus.ATIVO, tecnicoSemAprovacao.getDisponibilidade());
        }
    }
}
