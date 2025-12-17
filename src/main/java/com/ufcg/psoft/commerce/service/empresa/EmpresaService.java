package com.ufcg.psoft.commerce.service.empresa;

import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;

import java.util.List;

public interface EmpresaService {

    EmpresaResponseDTO alterar(Long id, String cnpj, String codigoAcesso, String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO);

    List<EmpresaResponseDTO> listar();

    EmpresaResponseDTO recuperar(String cnpj);

    EmpresaResponseDTO criar(Long id, String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO);

    void remover(Long id, String cnpj, String codigoAcesso, String senhaAdmin);

}
