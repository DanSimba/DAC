package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

public record SolicitacaoSagaDTO(
        String nome,
        String email,
        String cpf,
        String telefone,
        String salario,
        Endereco endereco
) {
}
