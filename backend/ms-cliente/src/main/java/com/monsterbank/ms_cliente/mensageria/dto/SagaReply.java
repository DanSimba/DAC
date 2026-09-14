package com.monsterbank.ms_cliente.mensageria.dto;

import tools.jackson.databind.JsonNode;

public record SagaReply(
        String sagaId,
        String tipo,
        String timestamp,
        String status,
        String erro,
        JsonNode payload

) {
}
