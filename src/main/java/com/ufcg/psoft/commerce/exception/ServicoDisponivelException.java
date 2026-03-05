package com.ufcg.psoft.commerce.exception;

public class ServicoDisponivelException extends CommerceException {
    public ServicoDisponivelException() {
        super("Serviço ja está disponivel");
    }
}
