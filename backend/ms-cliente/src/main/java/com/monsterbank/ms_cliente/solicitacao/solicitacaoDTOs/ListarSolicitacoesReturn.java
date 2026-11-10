package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public class ListarSolicitacoesReturn extends RepresentationModel<ListarSolicitacoesReturn> {

    private final List<SolicitacaoHateoasResponse> solicitacoes;

    public ListarSolicitacoesReturn(List<SolicitacaoHateoasResponse> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    public List<SolicitacaoHateoasResponse> getSolicitacoes() {
        return solicitacoes;
    }
}
