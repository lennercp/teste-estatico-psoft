package com.ufcg.psoft.commerce.service.tecnico;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.model.DisponibilidadeStatus;
import com.ufcg.psoft.commerce.model.Empresa;

import java.util.List;

public interface TecnicoService {
    TecnicoResponseDTO criar(TecnicoPostPutRequestDTO tecnicoDTO);
    List<TecnicoResponseDTO> listar();
    TecnicoResponseDTO recuperar(Long id);
    TecnicoResponseDTO atualizar(Long id, String codigoAcesso, TecnicoPostPutRequestDTO tecnicoDTO);
    void remover(Long id, String codigoAcesso);
    void adicionarAprovacao(Long id, Empresa empresa);
    void adicionarRejeicao(Long id, Empresa empresa);
    TecnicoResponseDTO alterarDisponibilidade(
            Long id,
            String codigoAcesso,
            DisponibilidadeStatus disponibilidade);



}