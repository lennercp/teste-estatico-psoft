package com.ufcg.psoft.commerce.model;

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
public class Admin {

    @Id
    @Column(nullable = false, unique = true)
    private Long id = 1L;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String senha;
}
