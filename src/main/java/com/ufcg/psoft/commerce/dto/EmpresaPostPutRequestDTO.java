package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaPostPutRequestDTO {

    @JsonProperty("cnpj")
    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(
            regexp = "^\\d{14}$",
            message = "CNPJ deve conter exatamente 14 dígitos numéricos"
    )
    private String cnpj;

    @JsonProperty("nome_fantasia")
    @NotBlank(message = "Nome fantasia é obrigatório")
    private String nomeFantasia;

    @JsonProperty("codigo_acesso")
    @NotNull(message = "Código de acesso é obrigatório")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Código de acesso deve ter exatamente 6 dígitos numéricos"
    )
    private String codigoAcesso;
}
