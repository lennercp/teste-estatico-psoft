package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.state.StatusChamado;
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
public class Chamado {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("servico")
    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", referencedColumnName = "id", nullable = false)
    private Servico servico;

    @JsonProperty("endereco")
    @Column(nullable = false)
    private String endereco;

    @JsonProperty("empresa_cnpj")
    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_cnpj", referencedColumnName = "cnpj", nullable = false)
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    @JsonIgnore
    private Cliente cliente;

    @OneToOne(mappedBy = "chamado")
    private Pagamento pagamento;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = true) // nullable pois começa sem técnico
    private Tecnico tecnico;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusChamado status;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (this.dataCriacao == null) {
            this.dataCriacao = agora;
        }
        this.dataAtualizacao = agora;
    }

    public void cancelarStatus(Long clienteSolicitanteId) {
        this.status = (StatusChamado) this.status.cancelar(clienteSolicitanteId, this.cliente.getId());
    }

    public void avancarStatus() {
        this.status = (StatusChamado) this.status.avancar();
        this.dataAtualizacao = LocalDateTime.now();
    }
}
