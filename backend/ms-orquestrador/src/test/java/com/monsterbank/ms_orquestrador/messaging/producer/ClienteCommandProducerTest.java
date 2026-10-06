package com.monsterbank.ms_orquestrador.messaging.producer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ClienteCommandProducerTest {

    @Test
    void deveEnviarComandoParaFilaDoCliente() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        ClienteCommandProducer producer = new ClienteCommandProducer(rabbitTemplate);
        SagaCommand command = new SagaCommand(
                "saga-1",
                "cliente.criar",
                "2026-09-29T01:00:00",
                new ObjectMapper().createObjectNode()
        );

        producer.enviar(command);

        verify(rabbitTemplate).convertAndSend(RabbitConfig.CLIENTE_COMMAND_QUEUE, command);
    }
}
