package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.HistoricoAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

}

