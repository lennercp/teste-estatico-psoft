package com.ufcg.psoft.commerce.service.interesse;


import com.ufcg.psoft.commerce.dto.ServicoInteresseRequestDTO;

public interface InteresseService {
    void adicionarInteresse(String cnpj,
                                      String codigoAcesso,
                                      Long servicoId,
                                      ServicoInteresseRequestDTO dto);
}


