package com.monsterbank.ms_orquestrador.messaging.producer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ClienteCommandProducer {

    private final RabbitTemplate rabbitTemplate;

    public ClienteCommandProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(SagaCommand command){
        rabbitTemplate.convertAndSend(RabbitConfig.CLIENTE_COMMAND_QUEUE, command);
    }

    public void enviar(String sagaId, String tipo, JsonNode payload) {
        enviar(new SagaCommand(
                sagaId,
                tipo,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                payload
        ));
    }

}
