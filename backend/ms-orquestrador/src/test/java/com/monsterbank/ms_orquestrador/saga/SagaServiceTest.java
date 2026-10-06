package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaServiceTest {

    private final ClienteCommandProducer clienteCommandProducer = mock(ClienteCommandProducer.class);
    private final SagaService sagaService = new SagaService(clienteCommandProducer);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarPrimeiroComandoDaSagaAprovarCliente() {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        SagaCommand startCommand = new SagaCommand(
                "saga-1",
                SagaService.APROVAR_CLIENTE,
                "2026-10-06T13:45:12",
                payload
        );

        sagaService.iniciar(startCommand);

        verify(clienteCommandProducer).enviar(
                "saga-1",
                SagaService.APROVAR_SOLICITACAO,
                payload
        );
    }

    @Test
    void deveRejeitarSagaSemCpf() {
        SagaCommand startCommand = new SagaCommand(
                "saga-1",
                SagaService.APROVAR_CLIENTE,
                "2026-10-06T13:45:12",
                objectMapper.createObjectNode()
        );

        assertThatThrownBy(() -> sagaService.iniciar(startCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cpf e obrigatorio para aprovar cliente");
    }
}
