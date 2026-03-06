package com.ufcg.psoft.commerce.service.atribuicao;

import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;
import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
import com.ufcg.psoft.commerce.repository.ChamadoRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;

import jakarta.transaction.Transactional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtribuicaoServiceImpl implements AtribuicaoService {

    private final ChamadoRepository chamadoRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AtribuicaoServiceImpl(ChamadoRepository chamadoRepository, TecnicoRepository tecnicoRepository,
            ApplicationEventPublisher eventPublisher) {
        this.chamadoRepository = chamadoRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processarChamadoEmAndamento(Chamado chamado) {
        if (chamado.getStatus() != StatusChamado.AGUARDANDO_TECNICO) {
            return;
        }
        Optional<Tecnico> tecnicoRetorno = this.tecnicoRepository
                .findFirstByDisponibilidadeAndEmpresasAprovadorasIsNotEmptyOrderByDisponibilidadeAtualizadaEmAsc(
                        DisponibilidadeStatus.ATIVO);

        if (tecnicoRetorno.isPresent()) {
            Tecnico tecnico = tecnicoRetorno.get();

            chamado.setTecnico(tecnico);
            chamado.avancarStatus();
            tecnico.alterarDisponibilidade(DisponibilidadeStatus.DESCANSO);

            this.eventPublisher.publishEvent(new ChamadoEmAtendimentoEvent(chamado));
        } else {
            // TODO: Chamar notificação para o cliente
        }
    }

    @Override
    @Transactional
    public void processarTecnicoAtivo(Tecnico tecnico) {
        if (!tecnico.temEmpresasAprovadoras() || tecnico.getDisponibilidade() != DisponibilidadeStatus.ATIVO) {
            return;
        }

        Optional<Chamado> chamadoRetorno = this.chamadoRepository
                .findFirstByStatusOrderByDataAtualizacaoAsc(StatusChamado.AGUARDANDO_TECNICO);

        if (chamadoRetorno.isPresent()) {
            Chamado chamado = chamadoRetorno.get();

            chamado.setTecnico(tecnico);
            chamado.avancarStatus();
            tecnico.alterarDisponibilidade(DisponibilidadeStatus.DESCANSO);
        }
    }

}
