package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import java.util.Map;

public record ListarSolicitacoesReturn(
        String cpf,
        String nome,
        String salario,
        String status,
        String motivo,
        String data,
        Map<String, String> links
) {
}
