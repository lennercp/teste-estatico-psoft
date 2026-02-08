package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.MeioPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoPagamentoRequestDTO {
    @JsonProperty("metodo")
    @NotNull
    private MeioPagamento metodo;

    public MeioPagamento getMetodo() {
        return metodo;
    }
}
