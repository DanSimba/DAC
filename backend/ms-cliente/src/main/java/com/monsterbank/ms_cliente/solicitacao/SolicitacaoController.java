package com.monsterbank.ms_cliente.solicitacao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.registrarSolicitacaoRequest;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(
            SolicitacaoService solicitacaoService

    ) {
        this.solicitacaoService = solicitacaoService;
    }


    @PostMapping("/registrar")
    public ResponseEntity<Void> regitrarSolicitacao(@RequestBody registrarSolicitacaoRequest dto){
        solicitacaoService.registrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



}
