package com.ufcg.psoft.commerce.service.chamado;

import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;

public interface ChamadoEventListener {
    void handleChamadoEmAtendimento(ChamadoEmAtendimentoEvent event);
}
