package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.exception.DisponibilidadeTecnicoInvalida;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
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



    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "tecnico_empresas_aprovadoras",
            joinColumns = @JoinColumn(name = "tecnico_id"),
            inverseJoinColumns = @JoinColumn(name = "empresa_id")
    )
    private Set<Empresa> empresasAprovadoras = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "tecnico_empresas_reprovadoras",
            joinColumns = @JoinColumn(name = "tecnico_id"),
            inverseJoinColumns = @JoinColumn(name = "empresa_id")
    )
    private Set<Empresa> empresasReprovadoras = new HashSet<>();

//    @JsonProperty("disponibilidade")
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    @Builder.Default
//    private DisponibilidadeStatus disponibilidade = DisponibilidadeStatus.DESCANSO;
//
//    @JsonProperty("disponibilidadeAtualizadaEm")
//    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
//    private LocalDateTime disponibilidadeAtualizadaEm;

    @JsonProperty("disponibilidade")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisponibilidadeStatus disponibilidade;

    @JsonProperty("disponibilidadeAtualizadaEm")
    @Column(nullable = false)
    private LocalDateTime disponibilidadeAtualizadaEm;

    @PrePersist
    public void prePersist() {
        if (this.disponibilidade == null) {
            this.disponibilidade = DisponibilidadeStatus.DESCANSO;
        }
        if (this.disponibilidadeAtualizadaEm == null) {
            this.disponibilidadeAtualizadaEm = LocalDateTime.now();
        }
    }

    public void alterarDisponibilidade(
            DisponibilidadeStatus novaDisponibilidade) {

        if (novaDisponibilidade == null) {
            throw new DisponibilidadeTecnicoInvalida();
        }

        if (this.disponibilidade == novaDisponibilidade) {
            return;
        }

        this.disponibilidadeAtualizadaEm = LocalDateTime.now();
        this.disponibilidade = novaDisponibilidade;

    }


}