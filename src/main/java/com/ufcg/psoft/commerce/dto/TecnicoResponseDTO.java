package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Tecnico;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nomeCompleto")
    private String nomeCompleto;

    @JsonProperty("especialidade")
    private String especialidade;

    @JsonProperty("placaVeiculo")
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    private String tipoVeiculo;

    @JsonProperty("corVeiculo")
    private String corVeiculo;

    @JsonProperty("disponibilidade")
    private DisponibilidadeStatus disponibilidade;

    @JsonProperty("disponibilidadeAtualizadaEm")
    private LocalDateTime disponibilidadeAtualizadaEm;

    public TecnicoResponseDTO(Tecnico tecnico) {
        this.id = tecnico.getId();
        this.nomeCompleto = tecnico.getNomeCompleto();
        this.especialidade = tecnico.getEspecialidade();
        this.placaVeiculo = tecnico.getPlacaVeiculo();
        this.tipoVeiculo = tecnico.getTipoVeiculo();
        this.corVeiculo = tecnico.getCorVeiculo();
        this.disponibilidade = tecnico.getDisponibilidade();
        this.disponibilidadeAtualizadaEm = tecnico.getDisponibilidadeAtualizadaEm();
    }
}