package com.ufcg.psoft.commerce.exception;

public class StatusDesconhecidoException extends CommerceException  {
    public StatusDesconhecidoException() {
        super("Status do chamado serviço desconhecido");
    }
}
