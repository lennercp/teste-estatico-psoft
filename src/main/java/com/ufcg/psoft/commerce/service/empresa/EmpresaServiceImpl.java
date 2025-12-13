package com.ufcg.psoft.commerce.service.empresa;

import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.model.Empresa;
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

    @Override
    public EmpresaResponseDTO alterar(String cnpj, String codigoAcesso, EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {
        // Busca a empresa pelo CNPJ
        Empresa empresa = empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNaoExisteException::new);

        // Verifica se o código de acesso está correto
        if (!empresa.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        // Mapeia os dados do DTO para a entidade e salva
        modelMapper.map(empresaPostPutRequestDTO, empresa);
        empresaRepository.save(empresa);

        return modelMapper.map(empresa, EmpresaResponseDTO.class);
    }

    @Override
    public EmpresaResponseDTO criar(EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {
        // Mapeia o DTO para a entidade
        Empresa empresa = modelMapper.map(empresaPostPutRequestDTO, Empresa.class);
        empresaRepository.save(empresa);

        return modelMapper.map(empresa, EmpresaResponseDTO.class);
    }

    @Override
    public void remover(String cnpj, String codigoAcesso) {
        // Busca a empresa pelo CNPJ
        Empresa empresa = empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNaoExisteException::new);

        // Verifica se o código de acesso está correto
        if (!empresa.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        // Deleta a empresa
        empresaRepository.delete(empresa);
    }

    @Override
    public List<EmpresaResponseDTO> listar() {
        List<Empresa> empresas = empresaRepository.findAll();
        return empresas.stream()
                .map(EmpresaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaResponseDTO recuperar(String cnpj) {
        // Busca a empresa pelo CNPJ
        Empresa empresa = empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNaoExisteException::new);
        return new EmpresaResponseDTO(empresa);
    }
}

