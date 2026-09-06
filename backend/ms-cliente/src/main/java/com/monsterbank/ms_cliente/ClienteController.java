package com.monsterbank.ms_cliente;



import com.monsterbank.ms_cliente.cliente.ClienteService;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.listarSolicitacoesReturn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;

@RestController
@RequestMapping("/clientes")
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


    @GetMapping
    public ResponseEntity<List<ClienteRetorno>> listarClientes
            (@RequestParam(required = false) String field,
             @RequestParam(defaultValue = "0") int page)
    {

        return ResponseEntity.ok(clienteService.listarClientes(field, page));

    }

    @PostMapping
    public ResponseEntity<Void> regitrarSolicitacao(@RequestBody RegistrarSolicitacaoRequest dto){
        solicitacaoService.registrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/solicitacoes/{cpf}")
    public ResponseEntity<?> recusarSolicitacao(@PathVariable String cpf, @RequestBody RecusarSolicitacaoRequest dto){
        solicitacaoService.rejeitar(cpf, dto.motivo());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/solicitacoes")
    public ResponseEntity<List<listarSolicitacoesReturn>> getSolicitacaos(@RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(solicitacaoService.listarSolicitacoes(page));
    }




}
