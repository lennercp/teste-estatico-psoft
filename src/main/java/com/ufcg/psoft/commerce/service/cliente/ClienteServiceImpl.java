package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ClientePatchRequestDTO;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.HistoricoAssinatura;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.repository.HistoricoAssinaturaRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.model.TipoPlano;
import com.ufcg.psoft.commerce.model.TipoServico;
import com.ufcg.psoft.commerce.model.NivelUrgencia;
import com.ufcg.psoft.commerce.model.Servico;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final HistoricoAssinaturaRepository historicoAssinaturaRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final ServicoRepository servicoRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              ModelMapper modelMapper,
                              AuthService authService,
                              HistoricoAssinaturaRepository historicoAssinaturaRepository,
                              ServicoRepository servicoRepository) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.historicoAssinaturaRepository = historicoAssinaturaRepository;
        this.servicoRepository = servicoRepository;
    }

    @Override
    public ClienteResponseDTO alterar(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO) {
        authService.autenticarCliente(id, codigoAcesso);

        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);

        modelMapper.map(clientePostPutRequestDTO, cliente);
        clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO alterarParcial(Long id, String codigoAcesso, ClientePatchRequestDTO clientePatchRequestDTO) {
        authService.autenticarCliente(id, codigoAcesso);

        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);

        org.modelmapper.ModelMapper modelMapperLocal = new org.modelmapper.ModelMapper();
        modelMapperLocal.getConfiguration().setSkipNullEnabled(true);

        modelMapperLocal.map(clientePatchRequestDTO, cliente);

        clienteRepository.save(cliente);

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO novoCicloCobranca(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);

        if(cliente.getPlanoAgendado() != cliente.getPlanoAtual()){
            cliente.setPlanoAtual(cliente.getPlanoAgendado());

            registraHistoricoAssinatura(cliente);
            clienteRepository.save(cliente);
        }

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    private void registraHistoricoAssinatura(Cliente cliente){
        historicoAssinaturaRepository.save(HistoricoAssinatura.builder()
                                            .cliente(cliente)
                                            .planoNovo(cliente.getPlanoAtual())
                                            .build()
                                           );
    }

    @Override
    public ClienteResponseDTO criar(ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente cliente = modelMapper.map(clientePostPutRequestDTO, Cliente.class);
        cliente.setPlanoAtual(cliente.getPlanoAgendado());
        clienteRepository.save(cliente);

        registraHistoricoAssinatura(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codigoAcesso) {
        authService.autenticarCliente(id, codigoAcesso);

        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);

        clienteRepository.delete(cliente);
    }

    @Override
    public List<ClienteResponseDTO> listarPorNome(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContaining(nome);
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponseDTO> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO recuperar(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);
        return new ClienteResponseDTO(cliente);
    }

    @Override
    public List<ServicoResponseDTO> listarServicosDisponiveis(
            Long clienteId,
            String codigoAcesso,
            TipoServico tipoServico,
            NivelUrgencia nivelUrgencia,
            String empresaCnpj,
            Double precoMin,
            Double precoMax) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        List<TipoPlano> planosPermitidos;
        
        if (cliente.getPlanoAtual().equals(TipoPlano.PREMIUM)) {
            planosPermitidos = Arrays.asList(TipoPlano.BASICO, TipoPlano.PREMIUM, TipoPlano.AMBOS);
        } else {
            planosPermitidos = Arrays.asList(TipoPlano.BASICO, TipoPlano.AMBOS);
        }

        List<Servico> servicosEncontrados = servicoRepository.buscarComFiltros(
                planosPermitidos,
                tipoServico,
                nivelUrgencia,
                empresaCnpj,
                precoMin,
                precoMax
        );

        return servicosEncontrados.stream()
                .map(servico -> modelMapper.map(servico, ServicoResponseDTO.class))
                .collect(Collectors.toList());
    }

    
}
