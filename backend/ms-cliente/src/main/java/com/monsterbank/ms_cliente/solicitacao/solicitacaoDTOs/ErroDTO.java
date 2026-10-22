package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import java.time.LocalDateTime;

public record ErroDTO(
    Integer status,
    String erro,
    String mensagem,
    LocalDateTime timestamp
) {
    public ErroDTO(Integer status, String erro, String mensagem) {
        this(status, erro, mensagem, LocalDateTime.now());
    }
}