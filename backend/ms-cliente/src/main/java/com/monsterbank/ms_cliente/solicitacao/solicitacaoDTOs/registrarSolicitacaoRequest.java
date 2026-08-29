package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

public record RegistrarSolicitacaoRequest(
        String nome,
        String email,
        String cpf,
        String telefone,
        String salario,
        Endereco endereco
        // String logradouro,
        // Integer numero,
        // String complemento,
        // String cep,
        // String cidade,
        // String uf
) {}