package com.monsterbank.ms_cliente.cliente;


import org.springframework.beans.factory.annotation.Autowired;
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


    }


    public List<ClienteRetorno> procurarCliente(String field){

        Optional<ClienteEntity> clienteOptional = clienteRepository.findByCpf(field);

        if (clienteOptional.isPresent()) {
            ClienteEntity retorno = clienteOptional.get();
            List<ClienteRetorno> cliente = new ArrayList<ClienteRetorno>();

            //procurar saldo no MS-conta

            ClienteRetorno response = new ClienteRetorno(
                    retorno.getCpf(),
                    retorno.getNome(),
                    retorno.getCidade(),
                    retorno.getUf(),
                    "Mudar");

            cliente.add(response);
            return cliente;
        }

        Optional<List<ClienteEntity>> listaClientes = clienteRepository.findByNomeContaining(field);

    }

}
