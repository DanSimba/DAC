package com.monsterbank.ms_orquestrador.messaging.consumer;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.saga.SagaService;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaStartConsumerTest {

    @Test
    void deveEncaminharComandoParaIniciarSaga() {
        SagaService sagaService = mock(SagaService.class);
        SagaStartConsumer consumer = new SagaStartConsumer(sagaService);
        SagaCommand command = new SagaCommand(
                "saga-1",
                "aprovar-cliente",
                "2026-10-06T13:45:12",
                new ObjectMapper().createObjectNode().put("cpf", "12345678901")
        );

        consumer.receber(command);

        verify(sagaService).iniciar(command);
    }
}
