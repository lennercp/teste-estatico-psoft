package com.ufcg.psoft.commerce.service.empresa;

import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;

import java.util.List;

public interface EmpresaService {

    EmpresaResponseDTO alterar(String cnpj, String codigoAcesso, String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO);

    List<EmpresaResponseDTO> listar();

    EmpresaResponseDTO recuperar(String cnpj);

    EmpresaResponseDTO criar(String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO);

    void remover(String cnpj, String codigoAcesso, String senhaAdmin);

}
