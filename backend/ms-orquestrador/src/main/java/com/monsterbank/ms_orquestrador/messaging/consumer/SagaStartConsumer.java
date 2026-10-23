package com.monsterbank.ms_orquestrador.messaging.consumer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.saga.SagaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SagaStartConsumer {

    private final SagaService sagaService;

    public SagaStartConsumer(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @RabbitListener(queues = RabbitConfig.SAGA_COMMAND_QUEUE)
    public void receber(SagaCommand command) {
        sagaService.iniciar(command);
    }
}
