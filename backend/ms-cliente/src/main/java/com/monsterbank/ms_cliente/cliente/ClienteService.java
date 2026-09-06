package com.monsterbank.ms_cliente.cliente;


import com.monsterbank.ms_cliente.exception.ErroCriacaClienteException;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SolicitacaoService solicitacaoService;

    public List<ClienteRetorno> listarClientes(Integer page){
        PageRequest peageable = PageRequest.of(page, 50);
        List<ClienteEntity> lista = clienteRepository.findAll(peageable).getContent();

        return lista.stream()
                .map(cliente -> new ClienteRetorno(
                        cliente.getCpf(),
                        cliente.getNome(),
                        cliente.getCidade(),
                        cliente.getUf(),
                        getSalario(cliente.getCpf())
                )).toList();

    }


    public List<ClienteRetorno> procurarCliente(String field){

        Optional<List<ClienteEntity>> clientesCPF = clienteRepository.findByCpfContaining(field);

        if (clientesCPF.isPresent()) {
            return clientesCPF.get().stream()
                    .map(cliente -> new ClienteRetorno(
                            cliente.getCpf(),
                            cliente.getNome(),
                            cliente.getCidade(),
                            cliente.getUf(),
                            getSalario(cliente.getCpf())
                    )).toList();
        }

        Optional<List<ClienteEntity>> clientesNome = clienteRepository.findByNomeContaining(field);

        return clientesNome.map(clienteEntities -> clienteEntities.stream()
                .map(cliente -> new ClienteRetorno(
                        cliente.getCpf(),
                        cliente.getNome(),
                        cliente.getCidade(),
                        cliente.getUf(),
                        getSalario(cliente.getCpf())
                )).toList()).orElseGet(ArrayList::new);

    }

    public void aprovarCliente(String cpf){



    }

    private void criarCliente(SolicitacaoEntity solicitacao){
        try{
            ClienteEntity cliente = new ClienteEntity(
                    solicitacao.getCpf(),
                    solicitacao.getNome(),
                    solicitacao.getEmail(),
                    solicitacao.getTelefone(),
                    solicitacao.getSalario(),
                    solicitacao.getLogradouro(),
                    solicitacao.getNumero(),
                    solicitacao.getComplemento(),
                    solicitacao.getCep(),
                    solicitacao.getCidade(),
                    solicitacao.getUf()
            );

            clienteRepository.save(cliente);
        }catch(ErroCriacaClienteException e){
            throw new RuntimeException("Erro ao criar cliente", e);
        }
    }

    private String getSalario(String cpf){
        //consulta conta
        return "12312321";
    }


}
