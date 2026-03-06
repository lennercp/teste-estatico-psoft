package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Empresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponseDTO {

    @JsonProperty("cnpj")
    private String cnpj;

    @JsonProperty("endereco")
    private String endereco;

    @JsonProperty("nome_fantasia")
    private String nomeFantasia;

    public EmpresaResponseDTO(Empresa empresa) {
        this.cnpj = empresa.getCnpj();
        this.nomeFantasia = empresa.getNomeFantasia();
        this.endereco = empresa.getEndereco();
    }

}
