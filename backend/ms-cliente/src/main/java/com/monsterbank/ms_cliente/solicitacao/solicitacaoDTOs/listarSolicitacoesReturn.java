package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

public record listarSolicitacoesReturn(
        String cpf,
        String nome,
        String salario,
        String status,
        String motivo,
        String data
) {
}
