package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.ChamadoResponseDTO;
import com.ufcg.psoft.commerce.events.ChamadoEmAtendimentoEvent;
import com.ufcg.psoft.commerce.model.Chamado;
import com.ufcg.psoft.commerce.service.chamado.ChamadoEventListenerImpl;
import com.ufcg.psoft.commerce.service.notificacoes.NotificacoesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChamadoEventListenerImplTest {
    @Mock
    private NotificacoesServiceImpl notificacaoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChamadoEventListenerImpl listener;

    @Test
    void deveChamarNotificacaoQuandoEventoOcorrer() {

        Chamado chamado = new Chamado();
        ChamadoResponseDTO dto = new ChamadoResponseDTO();

        when(modelMapper.map(chamado, ChamadoResponseDTO.class))
                .thenReturn(dto);

        listener.handleChamadoEmAtendimento(
                new ChamadoEmAtendimentoEvent(chamado)
        );

        verify(notificacaoService, times(1))
                .notificaChamadoEmAtendimento(dto);
    }
}
