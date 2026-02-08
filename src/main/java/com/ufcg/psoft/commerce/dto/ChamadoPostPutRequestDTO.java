package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ChamadoPostPutRequestDTO {

    @JsonProperty("servico")
    @NotBlank(message = "Servico obrigatorio")
    private long servico_id;

    @JsonProperty("empresa_cnpj")
    @NotBlank(message = "CNPJ da empresa obrigatorio")
    private String empresa_cnpj;

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
        return empresa_cnpj;
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
