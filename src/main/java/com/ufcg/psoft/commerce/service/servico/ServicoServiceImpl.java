package com.ufcg.psoft.commerce.service.servico;

import com.ufcg.psoft.commerce.dto.ServicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.exception.ServicoJaExisteException;
import com.ufcg.psoft.commerce.exception.ServicoNaoExisteException;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Servico;
import com.ufcg.psoft.commerce.model.TipoPlano;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.repository.ServicoRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository servicoRepository;
    private final EmpresaRepository empresaRepository;
    private final InteresseRepository interesseRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    public ServicoServiceImpl(ServicoRepository servicoRepository,
            EmpresaRepository empresaRepository,
            InteresseRepository interesseRepository,
            ModelMapper modelMapper,
            AuthService authService) {
        this.servicoRepository = servicoRepository;
        this.empresaRepository = empresaRepository;
        this.interesseRepository = interesseRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
    }

    @Override
    public ServicoResponseDTO criar(String cnpj,
            String codigoAcesso,
            ServicoPostPutRequestDTO dto) {

        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        if (servicoRepository.existsByNomeAndEmpresa(dto.getNome(), empresa)) {
            throw new ServicoJaExisteException();
        }

        Servico servico = modelMapper.map(dto, Servico.class);
        servico.setEmpresa(empresa);
        servico.setAtivo(true);

        servicoRepository.save(servico);

        return modelMapper.map(servico, ServicoResponseDTO.class);
    }

    @Override
    public List<ServicoResponseDTO> listar(String cnpj) {

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        return servicoRepository.findByEmpresa(empresa)
                .stream()
                .map(servico -> modelMapper.map(servico, ServicoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServicoResponseDTO buscar(String cnpj, Long servicoId) {

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        Servico servico = servicoRepository.findByIdAndEmpresa(servicoId, empresa)
                .orElseThrow(ServicoNaoExisteException::new);

        return modelMapper.map(servico, ServicoResponseDTO.class);
    }

    @Override
    public ServicoResponseDTO alterar(String cnpj,
            String codigoAcesso,
            Long servicoId,
            ServicoPostPutRequestDTO dto) {

        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        Servico servico = servicoRepository.findByIdAndEmpresa(servicoId, empresa)
                .orElseThrow(ServicoNaoExisteException::new);

        if (!servico.getNome().equals(dto.getNome())
                && servicoRepository.existsByNomeAndEmpresa(dto.getNome(), empresa)) {
            throw new ServicoJaExisteException();
        }

        modelMapper.map(dto, servico);
        servicoRepository.save(servico);

        return modelMapper.map(servico, ServicoResponseDTO.class);
    }

    @Override
    public void remover(String cnpj,
            String codigoAcesso,
            Long servicoId) {

        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        Servico servico = servicoRepository.findByIdAndEmpresa(servicoId, empresa)
                .orElseThrow(ServicoNaoExisteException::new);

        servicoRepository.delete(servico);
    }

    @Override
    public ServicoResponseDTO alterarDisponibilidade(String cnpj, String codigoAcesso, Long servicoId,
            boolean disponivel) {
        authService.autenticarEmpresa(cnpj, codigoAcesso);
        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);
        Servico servico = servicoRepository.findByIdAndEmpresa(servicoId, empresa)
                .orElseThrow(ServicoNaoExisteException::new);
        boolean estavaIndisponivel = !servico.getAtivo();
        servico.setAtivo(disponivel);
        servicoRepository.save(servico);

        if (estavaIndisponivel && disponivel) {
            List<Interesse> interesses = interesseRepository.findByServicoAndNotificadoFalse(servico);
            interesses.sort(Comparator
                    .comparing((Interesse i) -> i.getCliente().getPlanoAtual() == TipoPlano.PREMIUM
                            ? 0
                            : 1)
                    .thenComparing(Interesse::getDataInteresse));
            for (Interesse interesse : interesses) {
                System.out.println("\n=======================================================");
                System.out.println("NOTIFICAÇÃO PARA: " + interesse.getCliente().getNome() +
                        " (Plano " + interesse.getCliente().getPlanoAtual() + ")");
                System.out.println("MOTIVO: O serviço '" + servico.getNome()
                        + "' voltou a ficar disponível!");
                System.out.println("=======================================================\n");

                interesse.setNotificado(true);
            }
            interesseRepository.saveAll(interesses);
        }

        return modelMapper.map(servico, ServicoResponseDTO.class);
    }
}
