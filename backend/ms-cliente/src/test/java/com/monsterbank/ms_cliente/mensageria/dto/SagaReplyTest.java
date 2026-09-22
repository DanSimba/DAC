package com.monsterbank.ms_cliente.mensageria.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class SagaReplyTest {

    @Test
    void respostaNaoDeveIdentificarOperacaoNoJson() {
        SagaReply reply = new SagaReply(
                "saga-1",
                "2026-09-22T18:00:00",
                "SUCESSO",
                null,
                new ObjectMapper().createObjectNode()
        );

        String json = new ObjectMapper().writeValueAsString(reply);

        assertThat(json).doesNotContain("\"tipo\"");
        assertThat(json).contains("\"sagaId\":\"saga-1\"");
    }
}
