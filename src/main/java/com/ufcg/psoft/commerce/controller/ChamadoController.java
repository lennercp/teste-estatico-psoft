package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.exception.AcessoNegadoException;
import com.ufcg.psoft.commerce.model.TipoUsuario;
import com.ufcg.psoft.commerce.service.chamado.ChamadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chamados")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService chamadoService;

    private AuthRequestDTO buildAuth(TipoUsuario tipo, Long clienteId, String cnpj, String codigo) {
        return switch (tipo) {
            case CLIENTE -> AuthRequestDTO.cliente(clienteId, codigo);
            case EMPRESA -> AuthRequestDTO.empresa(cnpj, codigo);
            case ADMIN -> AuthRequestDTO.admin(codigo);
            default -> throw new AcessoNegadoException();
        };
    }

    @PostMapping
    public ResponseEntity<ChamadoResponseDTO> criarChamado(
            @Valid @RequestBody ChamadoPostPutRequestDTO dto,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = buildAuth(tipo, clienteId, null, codigo);
        ChamadoResponseDTO response = chamadoService.criar(dto, auth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> recuperarChamado(
            @PathVariable Long id,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader(value = "X-EMPRESA-CNPJ", required = false) String cnpj,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = buildAuth(tipo, clienteId, cnpj, codigo);
        return ResponseEntity.ok(chamadoService.recuperar(id, auth));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> atualizarChamado(
            @PathVariable Long id,
            @Valid @RequestBody ChamadoPatchRequestDTO dto,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader(value = "X-EMPRESA-CNPJ", required = false) String cnpj,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = buildAuth(tipo, clienteId, cnpj, codigo);
        return ResponseEntity.ok(chamadoService.atualizar(id, dto, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarChamado(
            @PathVariable Long id,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = buildAuth(tipo, clienteId, null, codigo);
        chamadoService.deletar(id, auth);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<Void> confirmarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody ChamadoPagamentoRequestDTO dto,
            @RequestHeader("X-CLIENT-ID") Long clienteId,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = AuthRequestDTO.cliente(clienteId, codigo);
        chamadoService.confirmarPagamento(id, dto, auth);
        return ResponseEntity.noContent().build();
    }
}
