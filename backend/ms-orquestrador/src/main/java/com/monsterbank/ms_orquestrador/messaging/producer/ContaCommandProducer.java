package com.monsterbank.ms_orquestrador.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.factory.SagaCommandFactory;

import tools.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component 
public class ContaCommandProducer extends BaseCommandProducer {

    public ContaCommandProducer(RabbitTemplate rabbitTemplate, SagaCommandFactory commandFactory) {
        super(rabbitTemplate, commandFactory, RabbitConfig.CONTA_COMMAND_QUEUE);
    }
}
