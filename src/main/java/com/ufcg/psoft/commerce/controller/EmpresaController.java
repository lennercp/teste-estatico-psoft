package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaResponseDTO;
import com.ufcg.psoft.commerce.service.empresa.EmpresaService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/{cnpj}")
    public ResponseEntity<EmpresaResponseDTO> recuperarEmpresa(
            @PathVariable String cnpj) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empresaService.recuperar(cnpj));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarEmpresas() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empresaService.listar());
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criarEmpresa(
            @RequestParam Long id,
            @RequestParam String senhaAdmin,
            @RequestBody @Valid EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        empresaService.criar(
                                id,
                                senhaAdmin,
                                empresaPostPutRequestDTO));
    }

    @PutMapping("/{cnpj}")
    public ResponseEntity<EmpresaResponseDTO> atualizarEmpresa(
            @RequestParam Long id,
            @PathVariable String cnpj,
            @RequestParam String codigoAcesso,

            @RequestParam String senhaAdmin,
            @RequestBody @Valid EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        empresaService.alterar(
                                id,
                                cnpj,
                                codigoAcesso,
                                senhaAdmin,
                                empresaPostPutRequestDTO));
    }

    @DeleteMapping("/{cnpj}")
    public ResponseEntity<Void> excluirEmpresa(
            @RequestParam Long id,
            @PathVariable String cnpj,
            @RequestParam String codigoAcesso,

            @RequestParam String senhaAdmin) {

        empresaService.remover(
                id,
                cnpj,
                codigoAcesso,
                senhaAdmin);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{cnpj}/aprovar/{tecnicoId}")
    public ResponseEntity<Void> aprovarTecnico(
            @PathVariable String cnpj,
            @PathVariable Long tecnicoId,
            @RequestParam String codigo) {

        empresaService.aprovarTecnico(cnpj, codigo, tecnicoId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{cnpj}/rejeitar/{tecnicoId}")
    public ResponseEntity<Void> rejeitarTecnico(
            @PathVariable String cnpj,
            @PathVariable Long tecnicoId,
            @RequestParam String codigo) {

        empresaService.rejeitarTecnico(cnpj, codigo, tecnicoId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
