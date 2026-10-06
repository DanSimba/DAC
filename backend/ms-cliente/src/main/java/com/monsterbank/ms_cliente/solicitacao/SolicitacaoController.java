package com.monsterbank.ms_cliente.solicitacao;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) { this.solicitacaoService = solicitacaoService; }

    @PostMapping
    public ResponseEntity<SolicitacaoEntity> registrarSolicitacao(@RequestBody RegistrarSolicitacaoRequest dto) {
        SolicitacaoEntity solicitacao = solicitacaoService.registrar(dto);

        URI location = URI.create("/solicitacoes/" + solicitacao.getCpf());

        return ResponseEntity.created(location).body(solicitacao);
    }

    @PostMapping("/{cpf}/rejeicao")
    public ResponseEntity<SolicitacaoEntity> rejeitarSolicitacao(@PathVariable String cpf, @RequestBody RecusarSolicitacaoRequest dto) {
        SolicitacaoEntity solicitacao = solicitacaoService.rejeitar(cpf, dto);

        return ResponseEntity.ok(solicitacao);
    }
}
