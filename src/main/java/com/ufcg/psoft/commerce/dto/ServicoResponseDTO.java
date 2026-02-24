package com.ufcg.psoft.commerce.dto;


import com.ufcg.psoft.commerce.model.NivelUrgencia;
import com.ufcg.psoft.commerce.model.TipoPlano;

import com.ufcg.psoft.commerce.model.TipoServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Servico;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("tipo")
    private TipoServico tipo;

    @JsonProperty("nivel_urgencia")
    private NivelUrgencia nivelUrgencia;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("preco_base")
    private Double precoBase;

    @JsonProperty("plano_disponivel")
    private TipoPlano tipoPlano;

    @JsonProperty("duracao_estimada")
    private Integer duracaoEstimada;

    @JsonProperty("ativo")
    private Boolean ativo;

    public ServicoResponseDTO(Servico servico) {
        this.id = servico.getId();
        this.nome = servico.getNome();
        this.tipo = servico.getTipo();
        this.nivelUrgencia = servico.getNivelUrgencia();
        this.descricao = servico.getDescricao();
        this.precoBase = servico.getPrecoBase();
        this.tipoPlano = servico.getTipoPlano();
        this.duracaoEstimada = servico.getDuracaoEstimada();
        this.ativo = servico.getAtivo();
    }
}
