package com.ufcg.psoft.commerce.service.tecnico;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.TecnicoNaoExisteException;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TecnicoResponseDTO criar(TecnicoPostPutRequestDTO tecnicoDTO) {
        Tecnico tecnico = modelMapper.map(tecnicoDTO, Tecnico.class);
        tecnicoRepository.save(tecnico);
        return modelMapper.map(tecnico, TecnicoResponseDTO.class);
    }

    @Override
    public List<TecnicoResponseDTO> listar() {
        List<Tecnico> tecnicos = tecnicoRepository.findAll();
        return tecnicos.stream()
                .map(tecnico -> modelMapper.map(tecnico, TecnicoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TecnicoResponseDTO recuperar(Long id) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);
        return modelMapper.map(tecnico, TecnicoResponseDTO.class);
    }

    @Override
    public TecnicoResponseDTO atualizar(Long id, String codigoAcesso, TecnicoPostPutRequestDTO tecnicoDTO) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);

        // Validação: Código de acesso deve bater
        if (!tecnico.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        // Atualiza os dados
        modelMapper.map(tecnicoDTO, tecnico);
        tecnico.setId(id); // Garante que o ID permanece o mesmo
        tecnicoRepository.save(tecnico);
        
        return modelMapper.map(tecnico, TecnicoResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codigoAcesso) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);

        // Validação da US3
        if (!tecnico.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        tecnicoRepository.delete(tecnico);
    }

    @Override
    public void adicionarAprovacao(Long id, Empresa empresa) {
        //TODO: colocar autenticação
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);

        tecnico.getEmpresasReprovadoras().remove(empresa);
        tecnico.getEmpresasAprovadoras().add(empresa);

        tecnicoRepository.save(tecnico);
    }

    @Override
    public void adicionarRejeicao(Long id, Empresa empresa) {
        //TODO: colocar autenticação
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);

        tecnico.getEmpresasAprovadoras().remove(empresa);
        tecnico.getEmpresasReprovadoras().add(empresa);

        tecnicoRepository.save(tecnico);
    }
}