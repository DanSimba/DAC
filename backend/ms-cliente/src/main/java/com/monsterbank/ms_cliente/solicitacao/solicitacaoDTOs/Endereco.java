package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

public record Endereco(
    String cep,
    String logradouro,
    Integer numero,
    String complemento,
    String bairro,
    String cidade,
    String uf
) {}
