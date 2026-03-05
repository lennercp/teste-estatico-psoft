package com.ufcg.psoft.commerce.exception;

public class ClienteNaoAutorizadoCancelarChamadoException extends CommerceException {

    public ClienteNaoAutorizadoCancelarChamadoException() {
        super("Somente o cliente que criou o chamado pode realizar o cancelamento.");
    }
}
