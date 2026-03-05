package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Tecnico;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    Optional<Tecnico> findFirstByDisponibilidadeOrderByDisponibilidadeAtualizadaEmAsc(
            DisponibilidadeStatus disponibilidade);
}