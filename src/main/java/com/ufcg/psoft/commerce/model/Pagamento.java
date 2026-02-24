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
public class Pagamento {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "chamado_id", nullable = false, unique = true)
    private Chamado chamado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeioPagamento metodo;

    @Column(nullable = false)
    private LocalDateTime confirmadoEm;

    public void setChamado(Chamado chamado) {
        this.chamado = chamado;
    }
}