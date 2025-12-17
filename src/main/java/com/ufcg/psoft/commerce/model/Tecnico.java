package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tecnicos")
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("nomeCompleto")
    @Column(nullable = false)
    private String nomeCompleto;

    @JsonProperty("especialidade")
    @Column(nullable = false)
    private String especialidade;

    @JsonProperty("placaVeiculo")
    @Column(nullable = false)
    private String placaVeiculo;

    @JsonProperty("tipoVeiculo")
    @Column(nullable = false)
    private String tipoVeiculo;

    @JsonProperty("corVeiculo")
    @Column(nullable = false)
    private String corVeiculo;

    @JsonProperty("codigoAcesso")
    @Column(nullable = false)
    private String codigoAcesso;
}