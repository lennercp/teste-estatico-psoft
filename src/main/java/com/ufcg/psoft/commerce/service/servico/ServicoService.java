package com.ufcg.psoft.commerce.service.servico;


import com.ufcg.psoft.commerce.dto.ServicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.model.Empresa;

import java.util.List;

public interface ServicoService {

    ServicoResponseDTO criar(String cnpj,
                             String codigoAcesso,
                             ServicoPostPutRequestDTO dto);

    List<ServicoResponseDTO> listar(String cnpj);

    ServicoResponseDTO buscar(String cnpj, Long servicoId);

    ServicoResponseDTO alterar(String cnpj,
                               String codigoAcesso,
                               Long servicoId,
                               ServicoPostPutRequestDTO dto);

    void remover(String cnpj,
                 String codigoAcesso,
                 Long servicoId);
}


