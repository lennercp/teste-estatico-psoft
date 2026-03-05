package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoPatchRequestDTO {

    @JsonProperty("endereco")
    private String endereco;

    @JsonProperty("tecnico_id")
    private Long tecnicoId; // Para quando a empresa/admin for atribuir um técnico

    @JsonProperty("status_acao")
    private String statusAcao; //"AVANCAR", "CANCELAR"

    @JsonProperty("codigo")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Codigo de acesso deve ter exatamente 6 digitos numericos"
    )
    private String codigo;

    public String getEndereco() {
        return endereco;
    }

    public String getCodigo() {
        return codigo;
    }

}
