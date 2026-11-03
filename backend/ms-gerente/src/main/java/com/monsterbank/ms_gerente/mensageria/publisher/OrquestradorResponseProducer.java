package com.monsterbank.ms_gerente.mensageria.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_gerente.mensageria.dto.SagaReply;
import com.monsterbank.ms_gerente.mensageria.enumeration.GerenteReplyQueue;

@Component 
public class OrquestradorResponseProducer {
    private final RabbitTemplate rabbitTemplate;

    public OrquestradorResponseProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(GerenteReplyQueue replyQueue, SagaReply reply) {
        rabbitTemplate.convertAndSend(replyQueue.queueName(), reply);
    }

}
