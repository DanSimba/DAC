package com.monsterbank.ms_account.query.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monsterbank.ms_account.mensageria.dto.EventMessageDTO;
import com.monsterbank.ms_account.query.entities.EventReadEntity;
import com.monsterbank.ms_account.query.services.ReadService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventConsumer {

    private final ReadService readService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventConsumer(
        ReadService readService
    ) {
        this.readService = readService;
    }

    @RabbitListener(queues = "ms.conta.eventos")
    @Transactional("readTransactionManager") 
    public void consume(EventMessageDTO message) {

        EventReadEntity ere = new EventReadEntity();

        ere.setObjetoId(message.getObjetoId());
        ere.setTipo(message.getTipo());
        ere.setPayload(objectMapper.valueToTree(message.getPayload()));
        ere.setVersao(message.getVersao());
        ere.setDatetime(message.getDatetime());

        readService.saveEvent(ere);
    }
}
