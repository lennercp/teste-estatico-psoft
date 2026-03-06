package com.ufcg.psoft.commerce.service.atribuicao;

import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.Tecnico;

public interface AtribuicaoService {
    void processarChamadoEmAndamento(Chamado chamado);

    void processarTecnicoAtivo(Tecnico tecnico);
}
