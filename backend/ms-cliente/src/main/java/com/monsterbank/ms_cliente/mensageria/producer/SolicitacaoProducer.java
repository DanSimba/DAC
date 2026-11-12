package com.monsterbank.ms_cliente.mensageria.producer;

import com.monsterbank.ms_cliente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_cliente.mensageria.enumeration.EmailCommandQueue;
import com.monsterbank.ms_cliente.mensageria.enumeration.GerenteCommandQueue;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class SolicitacaoProducer {
    private static final Logger log = LoggerFactory.getLogger(SolicitacaoProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    public SolicitacaoProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) { 
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper; 
    }

    public void enviarEmailRejeicao(String email, String motivo) {
        String COMMAND_TYPE = EmailCommandQueue.REJEITAR_SOLICITACAO.commandType();
        String QUEUE_NAME = EmailCommandQueue.REJEITAR_SOLICITACAO.queueName();

        log.info("{} --- Iniciando envio da solicitacao para o ms-email. Command: {} - Fila: {} ", SolicitacaoProducer.class, COMMAND_TYPE, QUEUE_NAME);

        try {
            java.util.Map<String, String> dadosEmail = java.util.Map.of(
                "email", email,
                "assunto", "Atualização sobre sua solicitação de conta",
                "mensagem", "Infelizmente sua solicitação foi recusada. Motivo: " + motivo
            );

            JsonNode payload = objectMapper.valueToTree(dadosEmail);
            String timestampString = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        
            SagaCommand command = new SagaCommand(
                null,
                QUEUE_NAME,
                timestampString,
                payload
            );

            rabbitTemplate.convertAndSend(COMMAND_TYPE, command);
            log.info("{} --- Enviando command: {}", SolicitacaoProducer.class, command);
            log.info("{} --- Enviando para fila: {}", SolicitacaoProducer.class, COMMAND_TYPE);
            log.info("{} --- Enviando para fila: {}", SolicitacaoProducer.class, QUEUE_NAME);
        } catch (Exception e) {
            log.error("{} --- Erro ao enviar comando de e-mail para a fila {}", SolicitacaoProducer.class, COMMAND_TYPE, e);
        }
    }
}
