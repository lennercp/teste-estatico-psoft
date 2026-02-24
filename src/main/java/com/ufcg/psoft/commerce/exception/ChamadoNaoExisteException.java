package com.ufcg.psoft.commerce.exception;

public class ChamadoNaoExisteException extends CommerceException {
    public ChamadoNaoExisteException() {
        super("O chamado consultado nao existe!");
    }
}
