package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import java.util.Map;

public record SolicitacaoHateoasResponse(
    String cpf,
    String nome,
    String email,
    String telefone,
    String salario,
    Endereco endereco,
    String status,
    String motivo,
    String dataHoraProcessamento,
    Map<String, Map<String, String>> _links
) {}