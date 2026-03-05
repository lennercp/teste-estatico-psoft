package com.ufcg.psoft.commerce.service.atribuicao;

import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
import com.ufcg.psoft.commerce.repository.ChamadoRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtribuicaoServiceImpl implements AtribuicaoService {

    private final ChamadoRepository chamadoRepository;
    private final TecnicoRepository tecnicoRepository;

    public AtribuicaoServiceImpl(ChamadoRepository chamadoRepository, TecnicoRepository tecnicoRepository) {
        this.chamadoRepository = chamadoRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Override
    public void processarChamadoEmAndamento(Chamado chamado) {
        Optional<Tecnico> tecnicoRetorno = this.tecnicoRepository
                .findFirstByDisponibilidadeOrderByDisponibilidadeAtualizadaEmAsc(DisponibilidadeStatus.ATIVO);

        if (tecnicoRetorno.isPresent()) {
            Tecnico tecnico = tecnicoRetorno.get();

            chamado.setTecnico(tecnico);
            chamado.avancarStatus();
            tecnico.alterarDisponibilidade(DisponibilidadeStatus.DESCANSO);
        } else {
            // TODO: Chamar notificação para o cliente
        }
    }

    @Override
    @Transactional
    public void processarTecnicoAtivo(Tecnico tecnico) {
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
