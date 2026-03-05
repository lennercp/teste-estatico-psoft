package com.ufcg.psoft.commerce.events;

import com.ufcg.psoft.commerce.model.Chamado;

public class ChamadoEmAtendimentoEvent {

    private final Chamado chamado;

    public ChamadoEmAtendimentoEvent(Chamado chamado) {
        this.chamado = chamado;
    }

    public Chamado getChamado() {
        return chamado;
    }
}