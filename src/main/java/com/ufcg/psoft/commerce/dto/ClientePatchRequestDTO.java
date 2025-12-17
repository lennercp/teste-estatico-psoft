package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.TipoPlano;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePatchRequestDTO {

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("endereco")
    private String endereco;

    @JsonProperty("codigo")
    @Pattern(regexp = "^\\d{6}$", message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codigo;

    @JsonProperty("plano")
    private TipoPlano planoAgendado;
}
