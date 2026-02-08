package com.ufcg.psoft.commerce.controller;


import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.empresa.EmpresaService;
import com.ufcg.psoft.commerce.service.servico.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/empresas/{cnpj}/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarServico(
            @PathVariable String cnpj,
            @PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(servicoService.buscar(cnpj, id));
    }

    @GetMapping
    public ResponseEntity<?> listarServicos(
            @PathVariable String cnpj) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(servicoService.listar(cnpj));
    }

    @PostMapping
    public ResponseEntity<?> criarServico(
            @PathVariable String cnpj,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid ServicoPostPutRequestDTO servicoPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        servicoService.criar(
                                cnpj,
                                codigoAcesso,
                                servicoPostPutRequestDTO
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarServico(
            @PathVariable String cnpj,
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid ServicoPostPutRequestDTO servicoPostPutRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        servicoService.alterar(
                                cnpj,
                                codigoAcesso,
                                id,
                                servicoPostPutRequestDTO
                        )
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirServico(
            @PathVariable String cnpj,
            @PathVariable Long id,
            @RequestParam String codigoAcesso) {

        servicoService.remover(
                cnpj,
                codigoAcesso,
                id

        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
