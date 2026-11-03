package com.monsterbank.ms_orquestrador.messaging.producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.factory.SagaCommandFactory;

import tools.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GerenteCommandProducer {
    private static final Logger log = LoggerFactory.getLogger(GerenteCommandProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final SagaCommandFactory commandFactory;

    public GerenteCommandProducer(RabbitTemplate rabbitTemplate, SagaCommandFactory commandFactory){
        this.rabbitTemplate = rabbitTemplate;
        this.commandFactory = commandFactory;
    }

    public void enviar(SagaCommand command){
        log.info("{} --- Enviando SAGA {}", ClienteCommandProducer.class, command);
        rabbitTemplate.convertAndSend(RabbitConfig.GERENTE_COMMAND_QUEUE, command);
    }

    public void enviar(String sagaId, String tipo, JsonNode payload) {
        log.info("{} --- Enviando SAGA {}", ClienteCommandProducer.class, sagaId);
        enviar(commandFactory.criar(sagaId, tipo, payload));
    }

}
