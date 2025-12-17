package com.ufcg.psoft.commerce.service.empresa;

import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final AuthService authService;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository,
                              ModelMapper modelMapper,
                              AuthService authService) {
        this.empresaRepository = empresaRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
    }

    @Override
    public EmpresaResponseDTO criar(String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {


        authService.autenticarAdmin(senhaAdmin);

        Empresa empresa = modelMapper.map(empresaPostPutRequestDTO, Empresa.class);
        empresaRepository.save(empresa);

        return modelMapper.map(empresa, EmpresaResponseDTO.class);
    }

    @Override
    public EmpresaResponseDTO alterar(String cnpj, String codigoAcesso, String senhaAdmin, EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {


        authService.autenticarAdmin(senhaAdmin);


        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        modelMapper.map(empresaPostPutRequestDTO, empresa);
        empresaRepository.save(empresa);

        return modelMapper.map(empresa, EmpresaResponseDTO.class);
    }

    @Override
    public void remover(String cnpj,
                        String codigoAcesso,
                        String senhaAdmin) {

        authService.autenticarAdmin(senhaAdmin);

        authService.autenticarEmpresa(cnpj, codigoAcesso);

        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(EmpresaNaoExisteException::new);

        empresaRepository.delete(empresa);
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
