package com.ufcg.psoft.commerce.repository;
import com.ufcg.psoft.commerce.model.Servico;
import com.ufcg.psoft.commerce.model.TipoPlano;
import com.ufcg.psoft.commerce.model.TipoServico;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.NivelUrgencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByEmpresa(Empresa empresa);

    boolean existsByNomeAndEmpresa(String nome, Empresa empresa);

    Optional<Servico> findByIdAndEmpresa(Long id, Empresa empresa);

    @Query("SELECT s FROM Servico s WHERE " +
           "s.tipoPlano IN :planosPermitidos AND " +
           "(:tipoServico IS NULL OR s.tipo = :tipoServico) AND " +
           "(:nivelUrgencia IS NULL OR s.nivelUrgencia = :nivelUrgencia) AND " +
           "(:empresaCnpj IS NULL OR s.empresa.cnpj = :empresaCnpj) AND " +
           "(:precoMin IS NULL OR s.precoBase >= :precoMin) AND " +
           "(:precoMax IS NULL OR s.precoBase <= :precoMax) " +
           "ORDER BY s.ativo DESC") 
    List<Servico> buscarComFiltros(
            @Param("planosPermitidos") List<TipoPlano> planosPermitidos,
            @Param("tipoServico") TipoServico tipoServico,
            @Param("nivelUrgencia") NivelUrgencia nivelUrgencia,
            @Param("empresaCnpj") String empresaCnpj,
            @Param("precoMin") Double precoMin,
            @Param("precoMax") Double precoMax
    );
}

