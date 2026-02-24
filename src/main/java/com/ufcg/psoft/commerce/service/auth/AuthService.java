package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.dto.AuthRequestDTO;

public interface AuthService {
    void autenticarCliente(Long id, String codigoAcesso);
    void autenticarTecnico(Long id, String codigoAcesso);
    void autenticarEmpresa(String cnpj, String codigoAcesso);
    void autenticarAdmin(Long id, String senha);
    void autenticar(AuthRequestDTO auth);
}
