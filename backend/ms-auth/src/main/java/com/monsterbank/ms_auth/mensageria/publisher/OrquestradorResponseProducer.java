package com.monsterbank.ms_auth.mensageria.publisher;

import com.monsterbank.ms_auth.enumeration.AuthReplyQueue;
import com.monsterbank.ms_auth.mensageria.dto.SagaReply;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrquestradorResponseProducer {

    private final RabbitTemplate  rabbitTemplate;

    public OrquestradorResponseProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }


    public void enviar(AuthReplyQueue replyQueue, SagaReply reply){

        rabbitTemplate.convertAndSend(
                replyQueue.queueName(),
                reply
        );

    }
}
