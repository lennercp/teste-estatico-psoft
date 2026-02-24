package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.NivelUrgencia;
import com.ufcg.psoft.commerce.model.TipoPlano;
import com.ufcg.psoft.commerce.model.TipoServico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoPostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome do serviço é obrigatório")
    private String nome;

    @JsonProperty("tipo")
    @NotNull(message = "Tipo do serviço é obrigatório")
    private TipoServico tipo;

    @JsonProperty("nivel_urgencia")
    @NotNull(message = "Nível de urgência é obrigatório")
    private NivelUrgencia nivelUrgencia;

    @JsonProperty("descricao")
    @NotBlank(message = "Descrição do serviço é obrigatória")
    private String descricao;

    @JsonProperty("preco_base")
    @NotNull(message = "Preço base é obrigatório")
    private Double precoBase;

    @JsonProperty("plano_disponivel")
    @NotNull(message = "Plano disponível é obrigatório")
    private TipoPlano tipoPlano;

    @JsonProperty("duracao_estimada")
    @NotNull(message = "Duração estimada é obrigatória")
    private Integer duracaoEstimada;
}
