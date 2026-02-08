package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ClientePatchRequestDTO;
import com.ufcg.psoft.commerce.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO; 
import com.ufcg.psoft.commerce.model.NivelUrgencia;  
import com.ufcg.psoft.commerce.model.TipoServico;

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

    List<ServicoResponseDTO> listarServicosDisponiveis(
            Long clienteId,
            String codigoAcesso,
            TipoServico tipoServico,
            NivelUrgencia nivelUrgencia,
            String empresaCnpj,
            Double precoMin,
            Double precoMax
    );
}
