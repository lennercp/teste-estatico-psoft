package com.ufcg.psoft.commerce.exception;

public class ChamadoJaCanceladoException extends CommerceException {

    public ChamadoJaCanceladoException() {
        super("O chamado já está cancelado.");
    }
}
