package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.model.TipoUsuario;
import com.ufcg.psoft.commerce.service.chamado.ChamadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chamados")
@RequiredArgsConstructor
public class ChamadoController {
    private final ChamadoService chamadoService;

    @PostMapping
    public ResponseEntity<ChamadoResponseDTO> criarChamado(
            @RequestBody ChamadoPostPutRequestDTO dto,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader(value = "X-EMPRESA-CNPJ", required = false) String cnpj,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = switch (tipo) {
            case CLIENTE -> AuthRequestDTO.cliente(clienteId, codigo);
            case EMPRESA -> AuthRequestDTO.empresa(cnpj, codigo);
            case ADMIN -> AuthRequestDTO.admin(codigo);
        };

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

        AuthRequestDTO auth = switch (tipo) {
            case CLIENTE -> AuthRequestDTO.cliente(clienteId, codigo);
            case EMPRESA -> AuthRequestDTO.empresa(cnpj, codigo);
            case ADMIN -> AuthRequestDTO.admin(codigo);
        };

        ChamadoResponseDTO response = chamadoService.recuperar(id, auth);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> atualizarChamado(
            @PathVariable Long id,
            @RequestBody ChamadoPatchRequestDTO dto,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader(value = "X-EMPRESA-CNPJ", required = false) String cnpj,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = switch (tipo) {
            case CLIENTE -> AuthRequestDTO.cliente(clienteId, codigo);
            case EMPRESA -> AuthRequestDTO.empresa(cnpj, codigo);
            case ADMIN -> AuthRequestDTO.admin(codigo);
        };

        return ResponseEntity.ok(chamadoService.atualizar(id, dto, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ChamadoResponseDTO> deletarChamado(
            @PathVariable Long id,
            @RequestHeader("X-USER-TYPE") TipoUsuario tipo,
            @RequestHeader(value = "X-CLIENT-ID", required = false) Long clienteId,
            @RequestHeader(value = "X-EMPRESA-CNPJ", required = false) String cnpj,
            @RequestHeader("X-ACCESS-CODE") String codigo) {

        AuthRequestDTO auth = switch (tipo) {
            case CLIENTE -> AuthRequestDTO.cliente(clienteId, codigo);
            case EMPRESA -> AuthRequestDTO.empresa(cnpj, codigo);
            case ADMIN -> AuthRequestDTO.admin(codigo);
        };

        chamadoService.deletar(id, auth);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<Void> confirmarPagamento(
            @PathVariable Long id,
            @RequestBody ChamadoPagamentoRequestDTO dto,
            @RequestHeader("X-CLIENT-ID") Long clienteId,
            @RequestHeader("X-ACCESS-CODE") String codigo
    ) {

        AuthRequestDTO auth = AuthRequestDTO.cliente(clienteId, codigo);

        chamadoService.confirmarPagamento(id, dto, auth);

        return ResponseEntity.noContent().build();
    }
}
