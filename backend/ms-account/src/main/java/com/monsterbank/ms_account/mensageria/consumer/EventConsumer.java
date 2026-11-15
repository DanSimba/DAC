package com.monsterbank.ms_account.mensageria.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monsterbank.ms_account.mensageria.dto.EventMessageDTO;
import com.monsterbank.ms_account.query.entities.AccountReadEntity;
import com.monsterbank.ms_account.query.entities.EventReadEntity;
import com.monsterbank.ms_account.query.services.ReadService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.monsterbank.ms_account.exceptions.*;

import com.monsterbank.ms_account.events.enums.EventType;
import com.monsterbank.ms_account.query.repositories.AccountReadRepository;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class EventConsumer {

    private final ReadService readService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountReadRepository accountReadRepository; //PRA GUARDAR CONTAS NO READ

    public EventConsumer(ReadService readService, AccountReadRepository acr) {
        this.readService = readService;
        this.accountReadRepository = acr;
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

        if (message.getTipo() == EventType.CRIADO) {

            JsonNode payload = objectMapper.valueToTree(message.getPayload());
            String clientCpf = payload.get("clientCpf").asText();
            
            if (accountReadRepository.findByClientCpf(clientCpf).isPresent()) {
                return;
            }

            AccountReadEntity acc = new AccountReadEntity();

            acc.setNumber(message.getObjetoId());
            acc.setClientCpf(payload.get("clientCpf").asText());
            acc.setBalanco(payload.get("saldo").decimalValue());
            acc.setManagerId(payload.get("cpfGerente").asLong());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            //PEGA DATA EM STRING
            LocalDateTime ldt =  LocalDateTime.parse(payload.get("data").asText(), formatter);

            acc.setCreatedAt(Integer.parseInt(ldt.format(DateTimeFormatter.BASIC_ISO_DATE)));

            accountReadRepository.save(acc);
        }
    }
}
