package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.TecnicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.TecnicoResponseDTO;
import com.ufcg.psoft.commerce.service.tecnico.TecnicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService){
        this.tecnicoService = tecnicoService;
    }

    @PostMapping
    public ResponseEntity<TecnicoResponseDTO> criarTecnico(
            @RequestBody @Valid TecnicoPostPutRequestDTO tecnicoDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tecnicoService.criar(tecnicoDTO));
    }

    @GetMapping
    public ResponseEntity<List<TecnicoResponseDTO>> listarTecnicos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tecnicoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> recuperarTecnico(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tecnicoService.recuperar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> atualizarTecnico(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid TecnicoPostPutRequestDTO tecnicoDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tecnicoService.atualizar(id, codigoAcesso, tecnicoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerTecnico(
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {
        tecnicoService.remover(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<TecnicoResponseDTO> alterarDisponibilidade(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid TecnicoPostPutRequestDTO dto) {

        return ResponseEntity.ok(
                tecnicoService.alterarDisponibilidade(
                        id,
                        codigoAcesso,
                        dto.getDisponibilidade()));
    }

}