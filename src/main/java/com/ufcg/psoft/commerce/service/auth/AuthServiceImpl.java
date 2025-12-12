package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService{
    private final ClienteRepository clienteRepository;

    public AuthServiceImpl(ClienteRepository c){
        this.clienteRepository = c;
    }

    @Override
    public void autenticarCliente(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);
        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }
}
