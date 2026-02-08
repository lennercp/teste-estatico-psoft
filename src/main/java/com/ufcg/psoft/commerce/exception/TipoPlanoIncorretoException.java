package com.ufcg.psoft.commerce.exception;

public class TipoPlanoIncorretoException extends RuntimeException {
    public TipoPlanoIncorretoException() {
        super("Precisa do plano Premium para esse serviço!");
    }
}
