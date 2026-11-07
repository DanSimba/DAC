package com.monsterbank.ms_account.events;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.events.EventEntity;

import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository er){
        this.eventRepository = er;
    }

    public void createCreateEvent(String accNumber, Long cpfGerente){

        //CRIA DATETIME
            LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
            String datetime = hoje.format(formatador);


        //CONSTRÓI O PAYLOAD
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("cpfGerente", cpfGerente);
            payload.put("saldo", BigDecimal.ZERO); //INICIA COM VALOR 0
            payload.put("dataCriacao", datetime);

        //COMO ACABOU DE CRIAR A VERSÃO TEM QUE SER 1
        EventEntity ee = new EventEntity(accNumber, EventType.CRIADO, payload, 1L);
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
    }

    public BigDecimal getSaldoAtual(String accNumber){
        //ENCONTRA A LISTA
        List<EventEntity> eventos = eventRepository.findByObjetoIdOrderByVersaoAsc(accNumber);

        BigDecimal saldo = BigDecimal.ZERO;

        for (EventEntity evento : eventos) {
            BigDecimal valor = evento.getPayload().get("valor").decimalValue();

            switch (evento.getTipo()) { 
                case DEPOSITO:
                    saldo = saldo.add(valor);
                    break;
                case TRANSFERENCIA_DESTINO:
                    saldo = saldo.add(valor);
                    break;
                case SAQUE:
                    saldo = saldo.subtract(valor);
                    break;
                case TRANSFERENCIA_ORIGEM:
                    saldo = saldo.subtract(valor);
                    break;
                default:
                    break;
            }
        }
        return saldo;
    }
}
