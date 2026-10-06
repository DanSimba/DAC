package com.monsterbank.ms_orquestrador.messaging.producer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.factory.SagaCommandFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class ClienteCommandProducer {

    private final RabbitTemplate rabbitTemplate;
    private final SagaCommandFactory commandFactory;

    public ClienteCommandProducer(RabbitTemplate rabbitTemplate, SagaCommandFactory commandFactory){
        this.rabbitTemplate = rabbitTemplate;
        this.commandFactory = commandFactory;
    }

    public void enviar(SagaCommand command){
        rabbitTemplate.convertAndSend(RabbitConfig.CLIENTE_COMMAND_QUEUE, command);
    }

    public void enviar(String sagaId, String tipo, JsonNode payload) {
        enviar(commandFactory.criar(sagaId, tipo, payload));
    }

}
