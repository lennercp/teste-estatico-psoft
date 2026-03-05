package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
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

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoResponseDTO {


    @JsonProperty("id")
    private Long id;

    @JsonProperty("servico")
    @NotBlank(message = "Servico obrigatorio")
    private long servico_id;

    @JsonProperty("empresa_cnpj")
    @NotBlank(message = "CNPJ da empresa obrigatorio")
    private String empresa_cnpj;

    @JsonProperty("endereco")
    @NotBlank(message = "Endereco obrigatorio")
    private String endereco;

    @JsonProperty("cliente_id")
    @NotNull(message = "Cliente obrigatorio")
    private long cliente_id;

    @JsonProperty("tecnico_id")
    private Long tecnicoId; // Pode ser null se ainda não houver técnico

    @JsonProperty("status")
    private String status; // Ex: RECEBIDO, EM_ANALISE, etc.

    @JsonProperty("data_criacao")
    private LocalDateTime dataCriacao;

    public ChamadoResponseDTO(Chamado chamado) {
        this.id = chamado.getId();
        this.servico_id = chamado.getServico().getId();
        this.empresa_cnpj = chamado.getEmpresa().getCnpj();
        this.endereco = chamado.getEndereco();
        this.cliente_id = chamado.getCliente().getId();
        this.dataCriacao = chamado.getDataCriacao();
        this.status = chamado.getStatus().getDescricao();

        this.tecnicoId = (chamado.getTecnico() != null) ? chamado.getTecnico().getId() : null;
    }
}
