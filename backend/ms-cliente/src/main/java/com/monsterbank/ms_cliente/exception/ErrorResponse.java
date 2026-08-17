package com.monsterbank.ms_cliente.exception;

public record ErrorResponse(
        int status,
        String mensagem
) {
}
