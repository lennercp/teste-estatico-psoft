package com.ufcg.psoft.commerce.exception;

public class AdminNaoExisteException extends CommerceException {
    public AdminNaoExisteException() {
        super("Admin não existe!");
    }
}
