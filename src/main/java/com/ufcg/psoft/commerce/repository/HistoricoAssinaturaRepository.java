package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.HistoricoAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoAssinaturaRepository extends JpaRepository<HistoricoAssinatura, Long> {
    List<HistoricoAssinatura> findByClienteId(Long clienteId);
}
