package com.ufcg.psoft.commerce.dto;

import com.ufcg.psoft.commerce.model.TipoUsuario;

public class AuthRequestDTO {

    private TipoUsuario tipo;
    private Long clienteId;
    private String empresaCnpj;
    private String codigoAcesso;

    private AuthRequestDTO() {}

    public static AuthRequestDTO cliente(Long id, String codigo) {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.tipo = TipoUsuario.CLIENTE;
        dto.clienteId = id;
        dto.codigoAcesso = codigo;
        return dto;
    }

    public static AuthRequestDTO empresa(String cnpj, String codigo) {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.tipo = TipoUsuario.EMPRESA;
        dto.empresaCnpj = cnpj;
        dto.codigoAcesso = codigo;
        return dto;
    }

    public static AuthRequestDTO admin(String codigo) {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.tipo = TipoUsuario.ADMIN;
        dto.codigoAcesso = codigo;
        return dto;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getEmpresaCnpj() {
        return empresaCnpj;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }
}

