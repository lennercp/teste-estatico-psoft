package com.ufcg.psoft.commerce.model.state;

public interface StatusComportamento {
    StatusComportamento avancar();
    StatusComportamento cancelar(Long solicitanteId, Long donoId);
    String getDescricao();
}
