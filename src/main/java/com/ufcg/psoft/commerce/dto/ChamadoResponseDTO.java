package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Chamado;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class ChamadoResponseDTO {


    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("servico")
    @NotBlank(message = "Servico obrigatorio")
    private String servico;

    @JsonProperty("empresa_cnpj")
    @NotBlank(message = "CNPJ da empresa obrigatorio")
    private String empresa_cnpj;

    @JsonProperty("endereco")
    @NotBlank(message = "Endereco obrigatorio")
    private String endereco;

    @JsonProperty("cliente_id")
    @NotNull(message = "Cliente obrigatorio")
    private long cliente_id;

    public ChamadoResponseDTO(Chamado chamado) {
        this.id = chamado.getId();
        this.servico = chamado.getServico();
        this.empresa_cnpj = chamado.getEmpresa().getCnpj();
        this.endereco = chamado.getEndereco();
        this.cliente_id = chamado.getCliente().getId();
    }
}
