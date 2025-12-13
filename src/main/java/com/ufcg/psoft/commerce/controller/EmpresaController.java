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
@RequestMapping(
        value = "/empresas",
        produces = MediaType.APPLICATION_JSON_VALUE
)
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

    @GetMapping("")
    public ResponseEntity<?> listarEmpresas() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empresaService.listar());
    }


    @PostMapping()
    public ResponseEntity<?> criarEmpresa(
            @RequestBody @Valid EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empresaService.criar(empresaPostPutRequestDTO));
    }

    @PutMapping("/{cnpj}")
    public ResponseEntity<?> atualizarEmpresa(
            @PathVariable String cnpj,
            @RequestParam String codigo,
            @RequestBody @Valid EmpresaPostPutRequestDTO empresaPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empresaService.alterar(cnpj, codigo, empresaPostPutRequestDTO));
    }

    @DeleteMapping("/{cnpj}")
    public ResponseEntity<?> excluirEmpresa(
            @PathVariable String cnpj,
            @RequestParam String codigo) {

        empresaService.remover(cnpj, codigo);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }
}
