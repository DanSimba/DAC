package com.monsterbank.ms_cliente.cliente;


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

    private String getSalario(String cpf){
        //consulta conta
        return "12312321";
    }


}
