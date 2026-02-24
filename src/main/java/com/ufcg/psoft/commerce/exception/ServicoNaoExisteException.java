package com.ufcg.psoft.commerce.exception;

public class ServicoNaoExisteException extends CommerceException {

    public ServicoNaoExisteException() {
        super("O serviço consultado não existe!");
    }
}
