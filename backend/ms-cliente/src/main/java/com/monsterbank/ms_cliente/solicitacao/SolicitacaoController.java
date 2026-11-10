package com.monsterbank.ms_cliente.solicitacao;

import java.net.URI;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoHateoasResponse;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoResponseDTO;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.ListarSolicitacoesReturn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {
    private static final Logger log = LoggerFactory.getLogger(SolicitacaoController.class);

    private final SolicitacaoService solicitacaoService;
    private final SolicitacaoModelAssembler solicitacaoModelAssembler;

    public SolicitacaoController(
            SolicitacaoService solicitacaoService,
            SolicitacaoModelAssembler solicitacaoModelAssembler
    ) {
        this.solicitacaoService = solicitacaoService;
        this.solicitacaoModelAssembler = solicitacaoModelAssembler;
    }

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
    public ResponseEntity<SolicitacaoResponseDTO> registrarSolicitacao(@Valid @RequestBody RegistrarSolicitacaoRequest dto) {
        log.info("{} --- Entrei no Post para registrar solicitação.", SolicitacaoController.class);
        SolicitacaoEntity solicitacao = solicitacaoService.registrar(dto);

        URI location = URI.create("/solicitacoes/" + solicitacao.getCpf());

        return ResponseEntity.created(location).body(solicitacaoModelAssembler.toResponseModel(solicitacao));
    }

    // @PreAuthorize("hasRole('GERENTE')") -> Proteção da chamada
    @PostMapping("/{cpf}/rejeicao")
    public ResponseEntity<SolicitacaoResponseDTO> rejeitarSolicitacao(@PathVariable String cpf, @Valid @RequestBody RecusarSolicitacaoRequest dto) {
        log.info("{} --- Entrei no Post para rejeitar uma solicitação.", SolicitacaoController.class);
        SolicitacaoEntity solicitacao = solicitacaoService.rejeitar(cpf, dto);

        return ResponseEntity.ok(solicitacaoModelAssembler.toResponseModel(solicitacao));
    }

}
