package com.ufcg.psoft.commerce.service.empresa;

import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import com.ufcg.psoft.commerce.service.tecnico.TecnicoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final TecnicoService tecnicoService;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository,
                              ModelMapper modelMapper,
                              AuthService authService,
                              TecnicoService tecnicoService) {
        this.empresaRepository = empresaRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.tecnicoService = tecnicoService;
    }

    @Override
    public EmpresaResponseDTO criar(Long id, String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {


        authService.autenticarAdmin(id, senhaAdmin);

        Empresa empresa = modelMapper.map(empresaPostPutRequestDTO, Empresa.class);
        empresaRepository.save(empresa);

        return modelMapper.map(empresa, EmpresaResponseDTO.class);
    }

    @Override
    public EmpresaResponseDTO alterar(Long id, String cnpj, String codigoAcesso, String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {


        authService.autenticarAdmin(id, senhaAdmin);


        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        modelMapper.map(empresaPostPutRequestDTO, empresa);
        empresaRepository.save(empresa);

        return modelMapper.map(empresa, EmpresaResponseDTO.class);
    }

    @Override
    public void remover(Long id, String cnpj,
                        String codigoAcesso,
                        String senhaAdmin) {

        authService.autenticarAdmin(id, senhaAdmin);

        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        empresaRepository.delete(empresa);
    }


    @Override
    public void aprovarTecnico(String cnpj, String codigoAcesso, Long tecnicoId) {
        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNaoExisteException::new);
        tecnicoService.adicionarAprovacao(tecnicoId, empresa);
    }

    @Override
    public void rejeitarTecnico(String cnpj, String codigoAcesso, Long tecnicoId) {
        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNaoExisteException::new);
        tecnicoService.adicionarRejeicao(tecnicoId, empresa);
    }

    @Override
    public List<EmpresaResponseDTO> listar() {
        return empresaRepository.findAll()
                .stream()
                .map(EmpresaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaResponseDTO recuperar(String cnpj) {
        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);
        return new EmpresaResponseDTO(empresa);
    }
}
