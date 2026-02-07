package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.MetodoPagamento;
import jakarta.validation.constraints.NotNull;

public class ChamadoPagamentoRequestDTO {
    @JsonProperty("metodo")
    @NotNull
    private MetodoPagamento metodo;

    public MetodoPagamento getMetodo() {
        return metodo;
    }
}
