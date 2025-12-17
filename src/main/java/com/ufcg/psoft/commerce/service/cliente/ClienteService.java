package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ClientePatchRequestDTO;
import com.ufcg.psoft.commerce.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO alterar(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO);

    ClienteResponseDTO alterarParcial(Long id, String codigoAcesso, ClientePatchRequestDTO clientePatchRequestDTO);

    ClienteResponseDTO novoCicloCobranca(Long id);

    List<ClienteResponseDTO> listar();

    ClienteResponseDTO recuperar(Long id);

    ClienteResponseDTO criar(ClientePostPutRequestDTO clientePostPutRequestDTO);

    void remover(Long id, String codigoAcesso);

    List<ClienteResponseDTO> listarPorNome(String nome);
}
