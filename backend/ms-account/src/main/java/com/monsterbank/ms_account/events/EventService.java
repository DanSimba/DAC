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

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository er){
        this.eventRepository = er;
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
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
        this.eventRepository.save(ee);
    }

    public BigDecimal getBalancoAtual(String accNumber){
        //ENCONTRA A LISTA
        List<EventEntity> eventos = eventRepository.findByObjetoIdOrderByVersaoAsc(accNumber);

        BigDecimal balanco = BigDecimal.ZERO;

        for (EventEntity evento : eventos) {
            switch (evento.getTipo()) { 
                case DEPOSITO:
                    balanco = balanco.add( //SÓ PROCURA 'VALOR' NO PAYLOAD SE FOR UM DESSES TIPOS, EVITA PROCURAR O VALOR CASO ELE NÃO EXISTA NO PAYLOAD (TAVA DANDO NULLPOINTEREXCEPTION)
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                case TRANSFERENCIA_DESTINO:
                    balanco = balanco.add(
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                case SAQUE:
                    balanco = balanco.subtract(
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                case TRANSFERENCIA_ORIGEM:
                    balanco = balanco.subtract(
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                default:
                    break;
            }
        }
        return balanco;
    }

    public Long getLastGerente(String accNumber){ //RETORNA CPF DO DO GERENTE ATUAL
          List<EventEntity> eventos = eventRepository.findByObjetoIdOrderByVersaoAsc(accNumber);

          //NÃO PODE ESTAR VAZIO PQ TEM Q TER PELO MENOS O CRIADO
            if (eventos.isEmpty()) throw new EventoNaoEncontrado();

            Long gerCpf = null;

            for (EventEntity evento : eventos) {
                if (evento.getTipo() == EventType.CRIADO||evento.getTipo() == EventType.GERENTE_ALTERADO) {

                    var payload = evento.getPayload();//pega payload
                    var cpfNode = payload.get("cpfGerente");//pega campo cpfgerente

                    String cpfTexto = cpfNode.asText();//transforma em string, pq aparentemente é mto dificil pro javinha tranformar de payload pra Long direto

                    gerCpf = Long.parseLong(cpfTexto); //tranforma em Long

                    //System.out.println("CPF CONVERTIDO: " + gerCpf);
                }
            }

            if (gerCpf == null) throw new EventoNaoEncontrado();

            return gerCpf;
    }

    public EventDTO getLastEvent(String accNumber){
        EventEntity lastEvent = this.eventRepository.findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .orElseThrow(() -> new EventoNaoEncontrado());

        EventDTO leDTO = new EventDTO(
            lastEvent.getId(),
            lastEvent.getObjetoId(),
            lastEvent.getTipo(),
            lastEvent.getPayload(),
            lastEvent.getVersao(),
            lastEvent.getDatetime()
        );

        return leDTO;
    }
}
