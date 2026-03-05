package com.ufcg.psoft.commerce.exception;

public class AdminJaExisteException extends CommerceException {
    public AdminJaExisteException() {
        super("Já existe um admin cadastrado no sistema");
    }
}
