package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoAssinatura {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("planoNovo")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPlano planoNovo;

    @JsonProperty("dataHora")
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dataHora;

    @JsonProperty("cliente")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}