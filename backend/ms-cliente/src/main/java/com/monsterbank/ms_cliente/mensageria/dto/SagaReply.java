package com.monsterbank.ms_cliente.mensageria.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record SagaReply(
        String sagaId,
        String timestamp,
        String status,
        String erro,
        JsonNode payload

) {
}
