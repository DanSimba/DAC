package com.monsterbank.ms_account.mensageria.dto;

import tools.jackson.databind.JsonNode;

public record SagaCommand(
        String sagaId,
        String tipo,
        String timestamp,
        JsonNode payload
) {
}
