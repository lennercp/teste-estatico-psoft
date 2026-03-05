package com.ufcg.psoft.commerce.service.interesse;

import com.ufcg.psoft.commerce.dto.ServicoInteresseRequestDTO;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.ServicoDisponivelException;
import com.ufcg.psoft.commerce.exception.ServicoNaoExisteException;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Servico;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import org.springframework.stereotype.Service;

@Service
public class InteresseServiceImpl implements InteresseService {

    private final InteresseRepository interesseRepository;
    private final ServicoRepository servicoRepository;
    private final ClienteRepository clienteRepository;
    private final AuthService authService;

    public InteresseServiceImpl(InteresseRepository interesseRepository,
                                ServicoRepository servicoRepository,
                                ClienteRepository clienteRepository,
                                AuthService authService) {
        this.interesseRepository = interesseRepository;
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
        this.authService = authService;
    }

    @Override
    public void adicionarInteresse(String cnpj, String codigoAcesso, Long servicoId, ServicoInteresseRequestDTO dto) {
        this.authService.autenticarEmpresa(cnpj, codigoAcesso);
        this.authService.autenticarCliente(dto.getId(), dto.getCodigoAcesso());

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(ServicoNaoExisteException::new);

        if(servico.getAtivo()){
            throw new ServicoDisponivelException();
        }
        Cliente cliente = clienteRepository.findById(dto.getId())
                .orElseThrow(ClienteNaoExisteException::new);

        Interesse interesse = new Interesse(cliente, servico);
        interesseRepository.save(interesse);
    }
}
