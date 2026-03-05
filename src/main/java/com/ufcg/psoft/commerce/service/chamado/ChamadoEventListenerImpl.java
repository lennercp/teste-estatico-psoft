package com.ufcg.psoft.commerce.service.chamado;

import com.ufcg.psoft.commerce.dto.ChamadoResponseDTO;
import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;
import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.service.notificacoes.NotificacoesService;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ChamadoEventListenerImpl implements ChamadoEventListener{
    private final NotificacoesService notificacaoService;
    private final ModelMapper modelMapper;

    public ChamadoEventListenerImpl(NotificacoesService notificacaoService,
                                ModelMapper modelMapper) {
        this.notificacaoService = notificacaoService;
        this.modelMapper = modelMapper;
    }

    @EventListener
    @Override
    public void handleChamadoEmAtendimento(ChamadoEmAtendimentoEvent event) {

        Chamado chamado = event.getChamado();

        ChamadoResponseDTO dto =
                modelMapper.map(chamado, ChamadoResponseDTO.class);

        notificacaoService.notificaChamadoEmAtendimento(dto);
    }
}
