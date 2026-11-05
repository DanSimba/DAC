package com.monsterbank.ms_account.events;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.events.EventEntity;

import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository er){
        this.eventRepository = er;
    }

    public void createSaqueEvent(OperationEntity oe, String accNumber){

        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", oe.getValue());
            payload.put("dataOperacao", oe.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.SAQUE, payload, version);
    }

    public void createDepositoEvent(OperationEntity oe, String accNumber){
        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", oe.getValue());
            payload.put("dataOperacao", oe.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.DEPOSITO, payload, version);
    }

    public void createOriginTransferenceEvent(TransferenceEntity te, String accNumber){
        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", te.getValue());
            payload.put("dataOperacao", te.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.TRANSFERENCIA_ORIGEM, payload, version);
    }

    public void createDestinyTransferenceEvent(TransferenceEntity te, String accNumber){
        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", te.getValue());
            payload.put("dataOperacao", te.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.TRANSFERENCIA_DESTINO, payload, version);
    }
}
