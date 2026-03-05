package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {
    List<Interesse> findByServicoAndNotificadoFalse(Servico servico);
}
