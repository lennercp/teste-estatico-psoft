package com.ufcg.psoft.commerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interesse {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDateTime dataInteresse;

    private boolean notificado;

    public Interesse(Cliente c, Servico s){
        this.servico = s;
        this.cliente = c;
        this.dataInteresse = LocalDateTime.now();
        this.notificado = false;
    }
}