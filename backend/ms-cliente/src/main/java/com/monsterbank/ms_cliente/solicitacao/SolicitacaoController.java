package com.monsterbank.ms_cliente.solicitacao;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoHateoasResponse;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.ListarSolicitacoesReturn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {
    private static final Logger log = LoggerFactory.getLogger(SolicitacaoController.class);

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) { this.solicitacaoService = solicitacaoService; }

    // @PreAuthorize("hasRole('GERENTE')") -> Proteção da chamada
    @GetMapping
    public ResponseEntity<ListarSolicitacoesReturn> buscarSolicitacoes(
            @RequestParam(required = false) StatusSolicitacao status, 
            @RequestParam(defaultValue = "0") int page
        ) {
        log.info("{} --- Entrei no Get de busca de todas as solicitações.", SolicitacaoController.class);
        ListarSolicitacoesReturn solicitacoes = solicitacaoService.listarSolicitacoes(status, page);

        return ResponseEntity.ok(solicitacoes);
    }

    // @PreAuthorize("hasRole('GERENTE')") -> Proteção da chamada
    @GetMapping("/{cpf}")
    public ResponseEntity<SolicitacaoHateoasResponse> buscaSolicitacaoPorCpf(@PathVariable String cpf){
        log.info("{} --- Entrei no Get de busca de uma solicitação.", SolicitacaoController.class);

        SolicitacaoHateoasResponse solicitacao = solicitacaoService.getSolicitacaoByCpf(cpf);
        
        return ResponseEntity.ok(solicitacao);
    }

    // @PreAuthorize("hasRole('GERENTE')") -> Proteção da chamada
    @PostMapping
    public ResponseEntity<SolicitacaoEntity> registrarSolicitacao(@RequestBody RegistrarSolicitacaoRequest dto) {
        log.info("{} --- Entrei no Post para registrar solicitação.", SolicitacaoController.class);
        SolicitacaoEntity solicitacao = solicitacaoService.registrar(dto);

        URI location = URI.create("/solicitacoes/" + solicitacao.getCpf());

        return ResponseEntity.created(location).body(solicitacao);
    }

    // @PreAuthorize("hasRole('GERENTE')") -> Proteção da chamada
    @PostMapping("/{cpf}/rejeicao")
    public ResponseEntity<SolicitacaoEntity> rejeitarSolicitacao(@PathVariable String cpf, @RequestBody RecusarSolicitacaoRequest dto) {
        log.info("{} --- Entrei no Post para rejeitar uma solicitação.", SolicitacaoController.class);
        SolicitacaoEntity solicitacao = solicitacaoService.rejeitar(cpf, dto);

        return ResponseEntity.ok(solicitacao);
    }

    // @PreAuthorize("hasRole('GERENTE')") -> Proteção da chamada
    @PostMapping("/{cpf}/approvacao")
    public ResponseEntity<SolicitacaoEntity> aprovarSolicitacao(@PathVariable String cpf) {
        log.info("{} --- Entrei no Post para aprovar uma solicitação., SolicitacaoController.class");
        return null;
    }
}
