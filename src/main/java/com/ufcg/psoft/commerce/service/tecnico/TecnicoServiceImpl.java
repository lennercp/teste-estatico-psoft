package com.ufcg.psoft.commerce.service.tecnico;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.exception.TecnicoNaoExisteException;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    public TecnicoServiceImpl(TecnicoRepository tecnicoRepository,
                              AuthService authService,
                              ModelMapper modelMapper){
        this.tecnicoRepository = tecnicoRepository;
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

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
        authService.autenticarTecnico(id, codigoAcesso);
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);
        modelMapper.map(tecnicoDTO, tecnico);
        tecnico.setId(id);
        tecnicoRepository.save(tecnico);
        
        return modelMapper.map(tecnico, TecnicoResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codigoAcesso) {
        authService.autenticarTecnico(id, codigoAcesso);
        tecnicoRepository.deleteById(id);
    }

    @Override
    public void adicionarAprovacao(Long id, Empresa empresa) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);

        tecnico.getEmpresasReprovadoras().remove(empresa);
        tecnico.getEmpresasAprovadoras().add(empresa);

        tecnicoRepository.save(tecnico);
    }

    @Override
    public void adicionarRejeicao(Long id, Empresa empresa) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(TecnicoNaoExisteException::new);

        tecnico.getEmpresasAprovadoras().remove(empresa);
        tecnico.getEmpresasReprovadoras().add(empresa);

        tecnicoRepository.save(tecnico);
    }
}