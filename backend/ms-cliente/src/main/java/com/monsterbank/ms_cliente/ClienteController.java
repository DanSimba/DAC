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
            (@RequestParam(required = false) String field,
             @RequestParam(defaultValue = "0") int page)
    {

        return ResponseEntity.ok(clienteService.listarClientes(field, page));

    }

    //RETORNA SÓ A STRING NOME
    @GetMapping("/findNameByCpf/{cpf}")
     public ResponseEntity<String> findNameByCpf(@PathVariable("cpf") String cpf){
          return ResponseEntity.ok(this.clienteService.findNameByCpf(cpf));
     }

     //RETORNA O CLIENTE INTEIRO
     @GetMapping("/findByCpf/{cpf}")
     public ResponseEntity<ClienteRetorno> findByCpf(@PathVariable("cpf") String cpf){
          return ResponseEntity.ok(this.clienteService.findByCpf(cpf));
     }

    // @GetMapping("/solicitacoes")
    // public ResponseEntity<List<ListarSolicitacoesReturn>> getSolicitacaos(@RequestParam(defaultValue = "0") int page){
    //     return ResponseEntity.ok(solicitacaoService.listarSolicitacoes(page));
    // }

}
