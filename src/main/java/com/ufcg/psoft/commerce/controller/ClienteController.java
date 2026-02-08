package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.ClientePatchRequestDTO;
import com.ufcg.psoft.commerce.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.ufcg.psoft.commerce.dto.ServicoResponseDTO;
import com.ufcg.psoft.commerce.model.NivelUrgencia;
import com.ufcg.psoft.commerce.model.TipoServico;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/clientes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarCliente(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperar(id));
    }

    @GetMapping("")
    public ResponseEntity<?> listarClientes(
            @RequestParam(required = false, defaultValue = "") String nome) {

        if (nome != null && !nome.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(clienteService.listarPorNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listar());
    }

    @PostMapping()
    public ResponseEntity<?> criarCliente(
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(clientePostPutRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.alterar(id, codigo, clientePostPutRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(
            @PathVariable Long id,
            @RequestParam String codigo) {
        clienteService.remover(id, codigo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> alterarParcialCliente(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestBody @Valid ClientePatchRequestDTO ClientePatchRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.alterarParcial(id, codigo, ClientePatchRequestDTO));
    }

    @PatchMapping("/{id}/proxCiclo")
    public ResponseEntity<?> proxCicloCobrancaCliente(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.novoCicloCobranca(id));
    }

    @GetMapping("/{id}/servicos")
    public ResponseEntity<List<ServicoResponseDTO>> listarServicosDoCatalogo(
            @PathVariable("id") Long clienteId,
            @RequestParam("codigoAcesso") String codigoAcesso,
            @RequestParam(value = "tipoServico", required = false) TipoServico tipoServico,
            @RequestParam(value = "nivelUrgencia", required = false) NivelUrgencia nivelUrgencia,
            @RequestParam(value = "empresaCnpj", required = false) String empresaCnpj,
            @RequestParam(value = "precoMin", required = false) Double precoMin,
            @RequestParam(value = "precoMax", required = false) Double precoMax
    ) {
        List<ServicoResponseDTO> servicos = clienteService.listarServicosDisponiveis(
                clienteId,
                codigoAcesso,
                tipoServico,
                nivelUrgencia,
                empresaCnpj,
                precoMin,
                precoMax
        );

        return ResponseEntity.ok(servicos);
    }

}