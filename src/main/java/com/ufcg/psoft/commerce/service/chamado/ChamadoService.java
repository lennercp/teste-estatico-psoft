package com.ufcg.psoft.commerce.service.chamado;

//import com.ufcg.psoft.commerce.dto.ChamadoPatchRequestDTO;
import com.ufcg.psoft.commerce.dto.*;
import org.springframework.http.ResponseEntity;

public interface ChamadoService {

    ChamadoResponseDTO recuperar(Long id, AuthRequestDTO auth);

    ChamadoResponseDTO criar(ChamadoPostPutRequestDTO dto,
            AuthRequestDTO auth);

    ChamadoResponseDTO atualizar(long id, ChamadoPatchRequestDTO dto, AuthRequestDTO auth);

    ResponseEntity<Void> deletar(Long id, AuthRequestDTO auth);

    void confirmarPagamento(Long id, ChamadoPagamentoRequestDTO dto, AuthRequestDTO auth);
}
