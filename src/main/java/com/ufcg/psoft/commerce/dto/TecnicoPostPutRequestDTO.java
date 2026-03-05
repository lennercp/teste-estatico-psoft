package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoPostPutRequestDTO {

    @JsonProperty("nomeCompleto")
    @NotBlank(message = "O nome completo é obrigatório")
    private String nomeCompleto;

    @JsonProperty("especialidade")
    @NotBlank(message = "A especialidade é obrigatória")
    private String especialidade;

    @JsonProperty("placaVeiculo")
    @NotBlank(message = "A placa do veículo é obrigatória")
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @NotBlank(message = "O tipo de veículo é obrigatório")
    private String tipoVeiculo;

    @JsonProperty("corVeiculo")
    @NotBlank(message = "A cor do veículo é obrigatória")
    private String corVeiculo;

    @JsonProperty("codigoAcesso")
    @NotBlank(message = "O código de acesso é obrigatório")
    @Size(min = 6, max = 6, message = "O código de acesso deve ter exatamente 6 dígitos")
    @Pattern(regexp = "\\d{6}", message = "O código de acesso deve conter apenas números")
    private String codigoAcesso;

    @JsonProperty("disponibilidade")
    private DisponibilidadeStatus disponibilidade;
}