package com.ufcg.psoft.commerce.exception;

public class TecnicoNaoExisteException extends CommerceException {
    public TecnicoNaoExisteException() {
        super("O técnico consultado não existe!");
    }
}