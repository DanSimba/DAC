package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import java.util.List;

public record ListarSolicitacoesReturn(
        List<SolicitacaoHateoasResponse> solicitacoes
) {}