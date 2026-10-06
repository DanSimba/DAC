package com.monsterbank.ms_orquestrador.messaging.producer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.factory.SagaCommandFactory;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteCommandProducerTest {

    @Test
    void deveEnviarComandoParaFilaDoCliente() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        SagaCommandFactory commandFactory = mock(SagaCommandFactory.class);
        ClienteCommandProducer producer = new ClienteCommandProducer(rabbitTemplate, commandFactory);
        SagaCommand command = new SagaCommand(
                "saga-1",
                "cliente.criar",
                "2026-09-29T01:00:00",
                new ObjectMapper().createObjectNode()
        );

        producer.enviar(command);

        verify(rabbitTemplate).convertAndSend(RabbitConfig.CLIENTE_COMMAND_QUEUE, command);
    }

    @Test
    void deveUsarFactoryParaCriarEEnviarComando() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        SagaCommandFactory commandFactory = mock(SagaCommandFactory.class);
        ClienteCommandProducer producer = new ClienteCommandProducer(rabbitTemplate, commandFactory);
        var payload = new ObjectMapper().createObjectNode().put("cpf", "12345678901");
        SagaCommand command = new SagaCommand(
                "saga-1",
                "cliente.aprovar-solicitacao",
                "2026-10-06T13:45:12",
                payload
        );
        when(commandFactory.criar("saga-1", "cliente.aprovar-solicitacao", payload))
                .thenReturn(command);

        producer.enviar("saga-1", "cliente.aprovar-solicitacao", payload);

        verify(commandFactory).criar("saga-1", "cliente.aprovar-solicitacao", payload);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.CLIENTE_COMMAND_QUEUE, command);
    }
}
