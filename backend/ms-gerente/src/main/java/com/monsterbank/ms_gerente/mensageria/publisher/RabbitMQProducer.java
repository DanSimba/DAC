package com.monsterbank.ms_gerente.mensageria.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service 
public class RabbitMQProducer {


    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);
    private RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void  sendMessage(String message){
        LOGGER.info(String.format("Mensagem enviada -> %s", message));
        rabbitTemplate.convertAndSend("ms.gerente.cmd", message);
    }
}