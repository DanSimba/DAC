package com.monsterbank.ms_account.mensageria.producer;

import com.monsterbank.ms_account.mensageria.dto.SagaReply;
import com.monsterbank.ms_account.mensageria.enumeration.AccountReplyQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrquestradorResponseProducer {

    private final RabbitTemplate  rabbitTemplate;

    public OrquestradorResponseProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }


    public void enviar(AccountReplyQueue replyQueue, SagaReply reply){

        rabbitTemplate.convertAndSend(
                replyQueue.queueName(),
                reply
        );

    }
}
