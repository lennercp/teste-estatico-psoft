package com.ufcg.psoft.commerce.service.atribuicao;

import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.model.Tecnico;

public interface AtribuicaoService {
    public void processarChamadoEmAndamento(Chamado chamado);

    public void processarTecnicoAtivo(Tecnico tecnico);
}
