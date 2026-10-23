package com.monsterbank.ms_orquestrador.messaging.factory;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SagaCommandFactoryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarComandoComTimestampPadronizado() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-06T13:45:12Z"), ZoneOffset.UTC);
        SagaCommandFactory factory = new SagaCommandFactory(clock);

        SagaCommand command = factory.criar(
                "saga-1",
                "cliente.aprovar-solicitacao",
                objectMapper.createObjectNode().put("cpf", "12345678901")
        );

        assertThat(command.sagaId()).isEqualTo("saga-1");
        assertThat(command.tipo()).isEqualTo("cliente.aprovar-solicitacao");
        assertThat(command.timestamp()).isEqualTo("2026-10-06T13:45:12");
        assertThat(command.payload().get("cpf").asString()).isEqualTo("12345678901");
    }

    @Test
    void deveRejeitarDadosObrigatoriosAusentes() {
        SagaCommandFactory factory = new SagaCommandFactory(Clock.systemUTC());

        assertThatThrownBy(() -> factory.criar("", "cliente.criar", objectMapper.createObjectNode()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sagaId e obrigatorio");
    }
}
