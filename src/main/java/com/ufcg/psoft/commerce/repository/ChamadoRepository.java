package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ufcg.psoft.commerce.model.state.StatusChamado;

import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    Optional<Chamado> findFirstByStatusOrderByDataAtualizacaoAsc(StatusChamado status);
}
