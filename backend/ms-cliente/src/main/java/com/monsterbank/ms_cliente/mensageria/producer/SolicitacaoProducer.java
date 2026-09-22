package com.monsterbank.ms_cliente.mensageria.producer;

import com.monsterbank.ms_cliente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class SolicitacaoProducer {
    private static final Logger log = LoggerFactory.getLogger(SolicitacaoProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String FILA_GERENTE_CMD = "ms.gerente.cmd";
    
    public SolicitacaoProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) { 
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper; 
    }
    
    public void enviarParaAnalise(SolicitacaoSagaDTO SolicitacaoDto) {
        log.info("Iniciando envio da solicitacao para o ms-gerente. Fila {}", FILA_GERENTE_CMD);

        JsonNode payload = objectMapper.valueToTree(SolicitacaoDto);
        String timestampString = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();

        SagaCommand command = new SagaCommand(
            UUID.randomUUID().toString(),
            "gerente.avaliar-solicitacao",
            timestampString,
            payload
        );
        
        rabbitTemplate.convertAndSend(FILA_GERENTE_CMD, command);
        log.info("Enviando command: {}", command);
    }
}
