package com.monsterbank.ms_orquestrador.messaging.factory;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class SagaCommandFactory {

    private final Clock clock;

    public SagaCommandFactory() {
        this(Clock.systemDefaultZone());
    }

    SagaCommandFactory(Clock clock) {
        this.clock = clock;
    }

    public SagaCommand criar(String sagaId, String tipo, JsonNode payload) {
        if (sagaId == null || sagaId.isBlank()) {
            throw new IllegalArgumentException("sagaId e obrigatorio");
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("tipo e obrigatorio");
        }

        if (payload == null || payload.isNull()) {
            throw new IllegalArgumentException("payload e obrigatorio");
        }

        return new SagaCommand(
                sagaId,
                tipo,
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS).toString(),
                payload
        );
    }
}
