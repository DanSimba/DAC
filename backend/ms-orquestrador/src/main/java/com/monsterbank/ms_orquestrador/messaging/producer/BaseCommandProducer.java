package com.monsterbank.ms_orquestrador.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.factory.SagaCommandFactory;
import tools.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseCommandProducer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RabbitTemplate rabbitTemplate;
    private final SagaCommandFactory commandFactory;
    private final String filaDestino;

    protected BaseCommandProducer(RabbitTemplate rabbitTemplate, SagaCommandFactory commandFactory, String filaDestino) {
        this.rabbitTemplate = rabbitTemplate;
        this.commandFactory = commandFactory;
        this.filaDestino = filaDestino;
    }

    public void enviar(SagaCommand command) {
        log.info("{} --- Enviando SAGA para fila [{}]: {}", getClass().getSimpleName(), filaDestino, command);
        rabbitTemplate.convertAndSend(filaDestino, command);
    }

    public void enviar(String sagaId, String tipo, JsonNode payload) {
        log.info("{} --- Enviando SAGA [{}]", getClass().getSimpleName(), sagaId);
        enviar(commandFactory.criar(sagaId, tipo, payload));
    }
}