package com.monsterbank.ms_orquestrador.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.factory.SagaCommandFactory;

import tools.jackson.databind.JsonNode;

@Component 
public class EmailCommandProducer extends BaseCommandProducer {

    public EmailCommandProducer(RabbitTemplate rabbitTemplate, SagaCommandFactory commandFactory) {
        super(rabbitTemplate, commandFactory, null);
    }
}
