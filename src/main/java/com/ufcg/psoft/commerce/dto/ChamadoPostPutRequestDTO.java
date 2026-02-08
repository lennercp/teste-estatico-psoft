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
public class ChamadoPostPutRequestDTO {

    @JsonProperty("servico")
    @NotBlank(message = "Servico obrigatorio")
    private long servico_id;

    @JsonProperty("empresaCnpj")
    @NotBlank(message = "CNPJ da empresa obrigatorio")
    private String empresaCnpj;

    @JsonProperty("endereco")
    private String endereco;

    @JsonProperty("codigo")
    @NotNull(message = "Codigo de acesso obrigatorio")
    @Pattern(regexp = "^\\d{6}$", message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codigo;

    @JsonProperty("cliente_id")
    @NotNull(message = "Cliente obrigatorio")
    private long cliente_id;

    public String getEmpresaCnpj() {
        return empresaCnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public long getCliente_id() {
        return cliente_id;
    }

    public long getServico_id() {
        return servico_id;
    }

    public String getCodigoAcesso() {
        return codigo;
    }
};
