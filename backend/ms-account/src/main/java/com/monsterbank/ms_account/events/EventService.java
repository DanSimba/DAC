package com.monsterbank.ms_account.events;

import com.monsterbank.ms_account.events.eventDTOs.*;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.events.eventDTOs.EventDTO;
import com.monsterbank.ms_account.events.EventEntity;
import com.monsterbank.ms_account.events.enums.EventType;

import com.monsterbank.ms_account.exceptions.EventoNaoEncontrado;

import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;

@Service
public class EventService {

    private final EventRepository eventRepository;

    //PUBLICADOR NA FILA DE EVENTOS
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventService(EventRepository er, ApplicationEventPublisher applicationEventPublisher){
        this.eventRepository = er;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void createCreateEvent(String accNumber, Long cpfGerente){

        //CRIA DATETIME
            LocalDateTime hoje = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
            String datetime = hoje.format(formatador);


        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("cpfGerente", cpfGerente);
            payload.put("saldo", BigDecimal.ZERO); //INICIA COM VALOR 0
            payload.put("data", datetime);

        //COMO ACABOU DE CRIAR A VERSÃO TEM QUE SER 1
        EventEntity ee = new EventEntity(accNumber, EventType.CRIADO, payload, 1L);
        this.saveAndPublish(ee);
    }

    public void createSaqueEvent(OperationEntity oe, String accNumber){

        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", oe.getValue());
            payload.put("data", oe.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.SAQUE, payload, version);
        this.saveAndPublish(ee);
    }

    public void createDepositoEvent(OperationEntity oe, String accNumber){
        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", oe.getValue());
            payload.put("data", oe.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.DEPOSITO, payload, version);
        this.saveAndPublish(ee);
    }

    public void createOriginTransferenceEvent(TransferenceEntity te, String accNumber){
        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", te.getValue());
            payload.put("data", te.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.TRANSFERENCIA_ORIGEM, payload, version);
        this.saveAndPublish(ee);
    }

    public void createDestinyTransferenceEvent(TransferenceEntity te, String accNumber){
        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", te.getValue());
            payload.put("data", te.getDatetime());

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        EventEntity ee = new EventEntity(accNumber, EventType.TRANSFERENCIA_DESTINO, payload, version);
        this.saveAndPublish(ee);
    }

    public void createGerenteAlteradoEvent(String accNumber, Long cpfGerente){
        //CRIA DATETIME
            LocalDateTime hoje = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
            String datetime = hoje.format(formatador);


        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("cpfGerente", cpfGerente);
            payload.put("data", datetime);

        //ENCONTRA A VERSÃO
        Long version = eventRepository
        .findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .map(event -> event.getVersao() + 1) //ENCONTRA A ULTIMA VERSÃO E SOMA 1  
        .orElse(1L); //SE NÃO TIVER NENHUMA COMEÇA NO 1

        //COMO ACABOU DE CRIAR A VERSÃO TEM QUE SER 1
        EventEntity ee = new EventEntity(accNumber, EventType.GERENTE_ALTERADO, payload, version);
        this.saveAndPublish(ee);
    }

    //TODAS AS FUNÇÕES DE PEGAR (LER) EVENTOS FORAM MOVIDOS PRO READ SERVICE DO BANCO READ, AQUI SÓ CRIA, SALVA E PUBLICA

    //PUBLICAR
    private void saveAndPublish(EventEntity ee) {
        //SALAVA PRIMEIRO NO DB-ACC
        EventEntity saved = eventRepository.save(ee);

        EventDTO dto = new EventDTO(
            saved.getId(),
            saved.getObjetoId(),
            saved.getTipo(),
            saved.getPayload(),
            saved.getVersao(),
            saved.getDatetime()
        );

        //PUBLICA O DTO PRA FILA DO READ
        applicationEventPublisher.publishEvent(dto);
    }
}
