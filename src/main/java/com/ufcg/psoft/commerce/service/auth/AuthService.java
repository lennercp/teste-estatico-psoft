package com.ufcg.psoft.commerce.service.auth;

public interface AuthService {
    void autenticarCliente(Long id, String codigoAcesso);
    void autenticarEmpresa(String cnpj, String codigoAcesso);
    void autenticarAdmin(String senha);
}
