package com.ufcg.psoft.commerce.exception;

public class ChamadoConcluidoNaoCanceladoException extends CommerceException {
    public ChamadoConcluidoNaoCanceladoException() {
        super("Chamado concluído não pode ser cancelado");
    }
}
