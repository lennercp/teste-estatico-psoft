package com.ufcg.psoft.commerce.exception;


public class ChamadoJaPossuiPagamentoConfirmadoException extends CommerceException {
    public ChamadoJaPossuiPagamentoConfirmadoException() {
        super("Chamado já possui pagamento confirmado");
    }
}
