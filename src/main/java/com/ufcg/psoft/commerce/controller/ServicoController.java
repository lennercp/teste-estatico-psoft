package com.ufcg.psoft.commerce.controller;


import com.ufcg.psoft.commerce.dto.EmpresaPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoInteresseRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.service.empresa.EmpresaService;
import com.ufcg.psoft.commerce.service.interesse.InteresseService;
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
    private final InteresseService interesseService;

    public ServicoController(ServicoService servicoService, InteresseService interesseService) {
        this.servicoService = servicoService;
        this.interesseService = interesseService;
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

    @PostMapping("/{id}/interesse")
    public ResponseEntity<?> adicionarInteresse(
            @PathVariable String cnpj,
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid ServicoInteresseRequestDTO dto) {
        interesseService.adicionarInteresse(
                cnpj,
                codigoAcesso,
                id,
                dto
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
        }

    @PatchMapping("/{servicoId}/disponibilidade")
    public ResponseEntity<ServicoResponseDTO> alterarDisponibilidade(
            @PathVariable String cnpj,
            @PathVariable Long servicoId,
            @RequestParam String codigoAcesso,
            @RequestParam boolean disponivel) {
        
        ServicoResponseDTO resultado = servicoService.alterarDisponibilidade(cnpj, codigoAcesso, servicoId, disponivel);
        return ResponseEntity.ok(resultado);
    }
}
