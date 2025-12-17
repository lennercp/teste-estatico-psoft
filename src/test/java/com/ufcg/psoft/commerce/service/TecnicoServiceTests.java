package com.ufcg.psoft.commerce.service;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.service.auth.AuthService;

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
    private AuthService authService;

    @Mock
    private ModelMapper modelMapper;

    private Tecnico tecnico;
    private TecnicoPostPutRequestDTO tecnicoDTO;
    private TecnicoResponseDTO tecnicoResponseDTO;

    @BeforeEach
    void setup() {
        tecnico = Tecnico.builder()
                .id(1L)
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .build();

        tecnicoDTO = TecnicoPostPutRequestDTO.builder()
                .nomeCompleto("José da Silva")
                .especialidade("Eletricista")
                .placaVeiculo("ABC-1234")
                .tipoVeiculo("Carro")
                .corVeiculo("Branco")
                .codigoAcesso("123456")
                .build();

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
        when(modelMapper.map(tecnicoDTO, Tecnico.class)).thenReturn(tecnico);
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.criar(tecnicoDTO);

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
    @DisplayName("Deve atualizar técnico quando o código de acesso está correto")
    void testAtualizarTecnicoSucesso() {
        doNothing().when(authService).autenticarTecnico(eq(1L), eq("123456"));
        
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);
        doNothing().when(modelMapper).map(tecnicoDTO, tecnico);

        when(modelMapper.map(tecnico, TecnicoResponseDTO.class)).thenReturn(tecnicoResponseDTO);

        TecnicoResponseDTO resultado = tecnicoService.atualizar(1L, "123456", tecnicoDTO);

        assertNotNull(resultado);
        verify(authService, times(1)).autenticarTecnico(eq(1L), eq("123456"));
        verify(tecnicoRepository, times(1)).save(tecnico);
        
        verify(modelMapper, times(1)).map(tecnicoDTO, tecnico);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar técnico com código de acesso incorreto")
    void testAtualizarTecnicoCodigoInvalido() {
        
        doThrow(new CodigoDeAcessoInvalidoException())
            .when(authService).autenticarTecnico(eq(1L), eq("000000"));

        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            tecnicoService.atualizar(1L, "000000", tecnicoDTO);
        });

        verify(tecnicoRepository, never()).findById(any());
        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover técnico com sucesso")
    void testRemoverTecnicoSucesso() {
        doNothing().when(authService).autenticarTecnico(eq(1L), eq("123456"));

        tecnicoService.remover(1L, "123456");

        verify(authService, times(1)).autenticarTecnico(eq(1L), eq("123456"));
        verify(tecnicoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve falhar ao remover técnico com código de acesso incorreto")
    void testRemoverTecnicoCodigoInvalido() {
        doThrow(new CodigoDeAcessoInvalidoException())
            .when(authService).autenticarTecnico(eq(1L), eq("999999"));

        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            tecnicoService.remover(1L, "999999");
        });
        verify(tecnicoRepository, never()).deleteById(any());
        verify(tecnicoRepository, never()).delete(any());
    }
}