package com.monsterbank.ms_gerente.mensageria.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_gerente.gerente.GerenteDTO;
import com.monsterbank.ms_gerente.gerente.GerenteService;
import com.monsterbank.ms_gerente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_gerente.mensageria.enumeration.GerenteReplyQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component 
public class GerenteCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(GerenteCommandConsumer.class);

    public GerenteCommandConsumer() {

    }

    @RabbitListener(queues = "ms.gerente.cmd")
    public void receber(SagaCommand command) {

    }

}
