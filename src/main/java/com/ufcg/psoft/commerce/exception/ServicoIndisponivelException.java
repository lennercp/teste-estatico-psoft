package com.ufcg.psoft.commerce.exception;

public class ServicoIndisponivelException extends CommerceException {
    public ServicoIndisponivelException() {
        super("Serviço está indisponivel");
    }
}
