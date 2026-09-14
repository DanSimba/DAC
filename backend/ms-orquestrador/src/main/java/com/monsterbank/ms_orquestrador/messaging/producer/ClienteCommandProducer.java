package com.monsterbank.ms_orquestrador.messaging.producer;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClienteCommandProducer {

    private final RabbitTemplate rabbitTemplate;

    public ClienteCommandProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(Object menssage){
        rabbitTemplate.convertAndSend("ms.cliente.cmd", menssage);
    }



}
