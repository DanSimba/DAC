package com.monsterbank.ms_cliente.utils;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue clienteCmdQueue(){
        return new Queue("ms.cliente.cmd", true);
    }

    @Bean
    public Queue orquestradorReplayQueue(){
        return new Queue("orquestrador.replay", true);
    }
}
