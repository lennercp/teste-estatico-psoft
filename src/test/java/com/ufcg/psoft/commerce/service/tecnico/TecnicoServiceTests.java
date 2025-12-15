package com.ufcg.psoft.commerce.service.tecnico;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.TecnicoNaoExisteException;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.service.tecnico.TecnicoServiceImpl; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Técnicos")
class TecnicoServiceTests {

    @InjectMocks
    private TecnicoServiceImpl tecnicoService; 

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private ModelMapper modelMapper;

    private Tecnico tecnico;
    private TecnicoPostPutRequestDTO tecnicoDTO;
    private TecnicoResponseDTO tecnicoResponseDTO;

    @BeforeEach
    void setup() {
        // Entidade (Banco) 
        tecnico = Tecnico.builder()
                .id(1L)
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .build();

        // DTO de Entrada
        tecnicoDTO = TecnicoPostPutRequestDTO.builder()
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .build();

        // DTO de Saída
        tecnicoResponseDTO = TecnicoResponseDTO.builder()
                .id(1L)
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .build();
    }

    @Test
    @DisplayName("Deve criar um técnico com sucesso")
    void testCriarTecnico() {
        // Cenário
        when(modelMapper.map(tecnicoDTO, Tecnico.class)).thenReturn(tecnico);
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        // Ação
        TecnicoResponseDTO resultado = tecnicoService.criar(tecnicoDTO);

        // Verificação
        assertNotNull(resultado);
        assertEquals(tecnicoResponseDTO.getId(), resultado.getId());
        verify(tecnicoRepository, times(1)).save(tecnico);
    }

    @Test
    @DisplayName("Deve listar técnicos com sucesso")
    void testListarTecnicos() {
        when(tecnicoRepository.findAll()).thenReturn(List.of(tecnico));
        when(modelMapper.map(any(Tecnico.class), eq(TecnicoResponseDTO.class))).thenReturn(tecnicoResponseDTO);

        List<TecnicoResponseDTO> resultado = tecnicoService.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve recuperar técnico por ID com sucesso")
    void testRecuperarTecnicoPorId() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.recuperar(1L);

        assertNotNull(resultado);
        assertEquals(tecnico.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar recuperar ID inexistente")
    void testRecuperarTecnicoInexistente() {
        when(tecnicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TecnicoNaoExisteException.class, () -> {
            tecnicoService.recuperar(99L);
        });
    }

    @Test
    @DisplayName("Deve atualizar técnico quando o código de acesso está correto")
    void testAtualizarTecnicoSucesso() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.atualizar(1L, "123456", tecnicoDTO);

        assertNotNull(resultado);
        verify(tecnicoRepository, times(1)).save(tecnico);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar técnico com código de acesso incorreto")
    void testAtualizarTecnicoCodigoInvalido() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));

        // Tenta atualizar com senha errada
        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            tecnicoService.atualizar(1L, "000000", tecnicoDTO);
        });

        // Garante que o banco NÃO foi alterado
        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover técnico com sucesso")
    void testRemoverTecnicoSucesso() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));

        tecnicoService.remover(1L, "123456");

        verify(tecnicoRepository, times(1)).delete(tecnico);
    }

    @Test
    @DisplayName("Deve falhar ao remover técnico com código de acesso incorreto")
    void testRemoverTecnicoCodigoInvalido() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));

        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            tecnicoService.remover(1L, "999999");
        });

        verify(tecnicoRepository, never()).delete(any());
    }
}