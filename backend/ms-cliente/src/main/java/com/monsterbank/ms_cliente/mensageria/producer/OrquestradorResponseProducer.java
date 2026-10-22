package com.monsterbank.ms_cliente.mensageria.producer;

import com.monsterbank.ms_cliente.mensageria.dto.SagaReply;
import com.monsterbank.ms_cliente.mensageria.enumeration.ClienteReplyQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrquestradorResponseProducer {

    private final RabbitTemplate  rabbitTemplate;

    public OrquestradorResponseProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }


    public void enviar(ClienteReplyQueue replyQueue, SagaReply reply){

        rabbitTemplate.convertAndSend(
                replyQueue.queueName(),
                reply
        );

    }
}
