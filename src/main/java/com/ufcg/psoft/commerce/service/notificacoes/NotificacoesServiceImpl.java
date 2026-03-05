package com.ufcg.psoft.commerce.service.notificacoes;

import com.ufcg.psoft.commerce.dto.ChamadoResponseDTO;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacoesServiceImpl implements NotificacoesService{

    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;


    @Override
    public void notificaChamadoEmAtendimento(ChamadoResponseDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.getCliente_id())
                .orElse(null);

        if (cliente == null) {
            System.out.println("Cliente não encontrado. Notificação não enviada.");
            return;
        }

        Tecnico tecnico = tecnicoRepository.findById(dto.getTecnicoId())
                .orElse(null);

        if (tecnico == null) {
            System.out.println("Técnico não encontrado. Notificação não enviada.");
            return;
        }

        System.out.println("\n==============================================");
        System.out.println("NOTIFICAÇÃO AO CLIENTE");
        System.out.println("Destinatário: " + cliente.getNome());
        System.out.println("----------------------------------------------");
        System.out.println("Seu chamado #" + dto.getId() +
                " está agora EM ATENDIMENTO.");
        System.out.println();
        System.out.println("Técnico Responsável:");
        System.out.println("Nome: " + tecnico.getNomeCompleto());
        System.out.println();
        System.out.println("Veículo:");
        System.out.println("Tipo: " + tecnico.getTipoVeiculo());
        System.out.println("Placa: " + tecnico.getPlacaVeiculo());
        System.out.println("Cor: " + tecnico.getCorVeiculo());
        System.out.println("==============================================\n");
    }
}
