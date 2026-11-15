package com.monsterbank.ms_cliente;

import com.monsterbank.ms_cliente.cliente.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    //private final SolicitacaoService solicitacaoService;

    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService
            //, SolicitacaoService solicitacaoService

    ) {
        this.clienteService = clienteService;
        //this.solicitacaoService = solicitacaoService;
    }


    @GetMapping
    public ResponseEntity<List<ClienteRetorno>> listarClientes
            (@RequestParam(required = false) String field)
    {

        return ResponseEntity.ok(clienteService.listarClientes(field));

    }

    //RETORNA SÓ A STRING NOME
    @GetMapping("/findNameByCpf/{cpf}")
     public ResponseEntity<ClienteRetorno> findNameByCpf(@PathVariable("cpf") String cpf){
          return ResponseEntity.ok(this.clienteService.findNameByCpf(cpf));
     }

     //RETORNA O CLIENTE INTEIRO
     @GetMapping("/findByCpf/{cpf}")
     public ResponseEntity<ClienteRetorno> findByCpf(@PathVariable("cpf") String cpf){
          return ResponseEntity.ok(this.clienteService.findByCpf(cpf));
     }
}
