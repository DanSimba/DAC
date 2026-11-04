package com.monsterbank.ms_gerente.mensageria.dto;

import tools.jackson.databind.JsonNode;

public record SagaReply(
    String sagaId,
    String timestamp,
    String status,
    String erro,
    JsonNode payload
) {}
