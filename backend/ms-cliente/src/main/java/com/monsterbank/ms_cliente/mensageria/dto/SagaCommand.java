package com.monsterbank.ms_cliente.mensageria.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record SagaCommand(
        String sagaId,
        String tipo,
        String timestamp,
        JsonNode payload
) {
}
