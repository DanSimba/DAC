package com.monsterbank.ms_cliente;



import com.monsterbank.ms_cliente.cliente.ClienteService;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.registrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.listarSolicitacoesReturn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final SolicitacaoService solicitacaoService;

    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService,
            SolicitacaoService solicitacaoService

    ) {
        this.clienteService = clienteService;
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> regitrarSolicitacao(@RequestBody registrarSolicitacaoRequest dto){
        solicitacaoService.registrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/listarAutoCadastro")
    public ResponseEntity<?> getSolicitacaos(@RequestParam int page){
        List<listarSolicitacoesReturn> response = solicitacaoService.listarSolicitacoes(page);
    }

    @GetMapping("/listarClientes")
    public ResponseEntity<List<ClienteRetorno>> listarTodosClientes(@RequestParam int page){
        List<ClienteRetorno> response = clienteService.listarClientes(page);

    }


    @GetMapping("/buscarCliente")
    public ResponseEntity<List<ClienteRetorno>> procurarCliente(@RequestParam String field){
        List<ClienteRetorno> response = clienteService.procurarCliente(field);
    }

}
