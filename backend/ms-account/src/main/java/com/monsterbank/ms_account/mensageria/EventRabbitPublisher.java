package com.monsterbank.ms_account.mensageria;

import com.monsterbank.ms_account.mensageria.dto.*;

import com.monsterbank.ms_account.events.eventDTOs.EventDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class EventRabbitPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EventRabbitPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //SÓ MANDA PARA A QUEUE MS.CONTA.EVENTOS DEPOIS DE SER COMMITADO NO BANCO NORMAL
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(EventDTO evento) {

        //MAPEIA O PAYLOAD CORRETAMENTE (O JSON NODE NÃO ESTAVA SENDO CONVERTIDO CORRETAMENTE)
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> payload = mapper.convertValue(
            evento.getPayload(),
            new TypeReference<Map<String, Object>>() {} //TYPE REFENCE LEMBRA O TIPO DA CHAVE E VALOR PARA CADA ITEM DO MAP
        );

        EventMessageDTO message = new EventMessageDTO(
            evento.getId(),
            evento.getObjetoId(),
            evento.getTipo(),
            payload,
            evento.getVersao(),
            evento.getDatetime()
        );


        rabbitTemplate.convertAndSend(
            "",
            "ms.conta.eventos",
            message
        );

    }
}
