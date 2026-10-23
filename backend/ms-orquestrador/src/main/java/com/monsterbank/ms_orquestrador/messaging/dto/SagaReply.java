package com.monsterbank.ms_orquestrador.messaging.dto;

import tools.jackson.databind.JsonNode;

/**
 * Contrato das respostas publicadas pelo ms-cliente.
 * A operacao e identificada pela fila, pois a mensagem de resposta nao possui o campo tipo.
 */
public record SagaReply(
        String sagaId,
        String timestamp,
        String status,
        String erro,
        JsonNode payload
) {
    public boolean sucesso() {
        return "SUCESSO".equalsIgnoreCase(status);
    }
}
