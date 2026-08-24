package com.monsterbank.ms_cliente.cliente;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService

    ) {
        this.clienteService = clienteService;
    }

    @GetMapping("/listarClientes")
    public ResponseEntity<List<ClienteRetorno>> listarTodosClientes(){
        List<ClienteRetorno> response = clienteService.listarClientes(page);

    }


    @GetMapping("/buscarCliente")
    public ResponseEntity<List<ClienteRetorno>> procurarCliente(){
        List<ClienteRetorno> response = clienteService.procurarCliente(field);
    }

}
