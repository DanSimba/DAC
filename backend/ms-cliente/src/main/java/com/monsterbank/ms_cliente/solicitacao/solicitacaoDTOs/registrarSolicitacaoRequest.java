package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

public record registrarSolicitacaoRequest(
        String nome,
        String email,
        String cpf,
        String telefone,
        String salario,
        String logradouro,
        Integer numero,
        String complemento,
        String cep,
        String cidade,
        String uf
) {
}

