package com.ufcg.psoft.commerce.controller;


import com.ufcg.psoft.commerce.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.empresa.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    EmpresaService empresaService;

    @GetMapping("/{cnpj}")
    public ResponseEntity<?> recuperarEmpresa(
            @PathVariable String cnpj) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empresaService.recuperar(cnpj));
    }

    @GetMapping
    public ResponseEntity<?> listarEmpresas() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empresaService.listar());
    }

    @PostMapping
    public ResponseEntity<?> criarEmpresa(
            @RequestParam Long id,
            @RequestParam String senhaAdmin,
            @RequestBody @Valid EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        empresaService.criar(
                                id,
                                senhaAdmin,
                                empresaPostPutRequestDTO
                        )
                );
    }

    @PutMapping("/{cnpj}")
    public ResponseEntity<?> atualizarEmpresa(
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
                                empresaPostPutRequestDTO
                        )
                );
    }

    @DeleteMapping("/{cnpj}")
    public ResponseEntity<?> excluirEmpresa(
            @RequestParam Long id,
            @PathVariable String cnpj,
            @RequestParam String codigoAcesso,

            @RequestParam String senhaAdmin) {

        empresaService.remover(
                id,
                cnpj,
                codigoAcesso,
                senhaAdmin
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
