package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.TecnicoNaoExisteException;
import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.EmpresaNaoExisteException;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Tecnico;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.TecnicoRepository;
import com.ufcg.psoft.commerce.model.Empresa;
import com.ufcg.psoft.commerce.repository.EmpresaRepository;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService{
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final EmpresaRepository empresaRepository;

    public AuthServiceImpl(ClienteRepository c, TecnicoRepository t, EmpresaRepository e) {
        this.clienteRepository = c;
        this.tecnicoRepository = t;
        this.empresaRepository = e;
    }

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
        Empresa empresa = empresaRepository.findByCnpj(cnpj).
                orElseThrow(EmpresaNaoExisteException::new);
        if (!empresa.getCodigoAcesso().equals(codigoAcesso)) {
                throw  new CodigoDeAcessoInvalidoException();
        }

    }
}
