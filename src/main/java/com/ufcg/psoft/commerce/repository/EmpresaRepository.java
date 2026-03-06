package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCnpj(String cnpj);
}
