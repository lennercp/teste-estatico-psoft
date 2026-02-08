package com.ufcg.psoft.commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false, length = 6)
    private String codigoAcesso;
}
