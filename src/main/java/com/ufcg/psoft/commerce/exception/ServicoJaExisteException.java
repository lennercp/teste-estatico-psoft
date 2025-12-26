package com.ufcg.psoft.commerce.exception;

public class ServicoJaExisteException extends CommerceException {

  public ServicoJaExisteException() {
    super("Já existe um serviço com esse nome para esta empresa!");
  }
}

