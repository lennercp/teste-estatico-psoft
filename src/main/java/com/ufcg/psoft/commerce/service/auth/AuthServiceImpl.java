package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.exception.AdminSenhaInvalidaException;

import com.ufcg.psoft.commerce.exception.AdminNaoExisteException;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.repository.AdminRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService{

    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final AdminRepository adminRepository;

    public AuthServiceImpl(
            ClienteRepository c,
            EmpresaRepository e,
            AdminRepository a) {

        this.clienteRepository = c;
        this.empresaRepository = e;
        this.adminRepository = a;
    }


    @Override
    public void autenticarCliente(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);
        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }

    @Override
    public void autenticarEmpresa(String cnpj, String codigoAcesso) {
        Empresa empresa = empresaRepository.findByCnpj(cnpj).
                orElseThrow(EmpresaNaoExisteException::new);
        if (!empresa.getCodigoAcesso().equals(codigoAcesso)) {
            throw  new CodigoDeAcessoInvalidoException();
        }

    }

    @Override
    public void autenticarAdmin(Long id, String senha) {

        Admin admin = adminRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(AdminNaoExisteException::new);

        if (!admin.getSenha().equals(senha) || admin.getId() != id) {
            throw new AdminSenhaInvalidaException();
        }
    }
}
