package com.ufcg.psoft.commerce.exception;

public class ChamadoBloqueadoParaAvancarStatus extends CommerceException {

    public ChamadoBloqueadoParaAvancarStatus() {
        super("O chamado não pode avançar para o próximo estado.");
    }
}
