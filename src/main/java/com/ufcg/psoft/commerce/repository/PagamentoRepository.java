package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

}
