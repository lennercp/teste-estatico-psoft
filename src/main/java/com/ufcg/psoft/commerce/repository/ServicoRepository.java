package com.ufcg.psoft.commerce.repository;
import com.ufcg.psoft.commerce.model.Servico;
import com.ufcg.psoft.commerce.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByEmpresa(Empresa empresa);

    boolean existsByNomeAndEmpresa(String nome, Empresa empresa);

    Optional<Servico> findByIdAndEmpresa(Long id, Empresa empresa);
}

