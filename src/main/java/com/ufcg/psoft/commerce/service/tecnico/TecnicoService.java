package com.ufcg.psoft.commerce.service.tecnico;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import java.util.List;

public interface TecnicoService {
    TecnicoResponseDTO criar(TecnicoPostPutRequestDTO tecnicoDTO);
    List<TecnicoResponseDTO> listar();
    TecnicoResponseDTO recuperar(Long id);
    TecnicoResponseDTO atualizar(Long id, String codigoAcesso, TecnicoPostPutRequestDTO tecnicoDTO);
    void remover(Long id, String codigoAcesso);
}