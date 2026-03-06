package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.dto.AuthRequestDTO;
import com.ufcg.psoft.commerce.exception.AdminSenhaInvalidaException;

import com.ufcg.psoft.commerce.exception.AdminNaoExisteException;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.TecnicoNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.AdminRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final EmpresaRepository empresaRepository;
    private final AdminRepository adminRepository;

    @Override
    public void autenticarCliente(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);
        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }

    @Override
    public void autenticarTecnico(Long id, String codigoAcesso) {
        Tecnico tecnico = tecnicoRepository.findById(id).orElseThrow(TecnicoNaoExisteException::new);
        if (!tecnico.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }

    @Override
    public void autenticarEmpresa(String cnpj, String codigoAcesso) {
        Empresa empresa = empresaRepository.findByCnpj(cnpj).orElseThrow(EmpresaNaoExisteException::new);
        if (!empresa.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

    }

    @Override
    public void autenticarAdmin(Long id, String senha) {

        Admin admin = adminRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(AdminNaoExisteException::new);

        if (!admin.getSenha().equals(senha) || 1 != id) {
            throw new AdminSenhaInvalidaException();
        }
    }

    @Override
    public void autenticar(AuthRequestDTO auth) {
        switch (auth.getTipo()) {
            case ADMIN -> autenticarAdmin(1L, auth.getCodigoAcesso());
            case CLIENTE -> autenticarCliente(auth.getClienteId(), auth.getCodigoAcesso());
            case EMPRESA -> autenticarEmpresa(auth.getEmpresaCnpj(), auth.getCodigoAcesso());
            default -> throw new IllegalArgumentException("Tipo de usuário inválido: " + auth.getTipo());
        }
    }
}
