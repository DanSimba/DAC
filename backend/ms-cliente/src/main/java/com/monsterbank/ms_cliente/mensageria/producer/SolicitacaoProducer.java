package com.monsterbank.ms_cliente.mensageria.producer;

import com.monsterbank.ms_cliente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_cliente.mensageria.enumeration.EmailCommandQueue;
import com.monsterbank.ms_cliente.mensageria.enumeration.GerenteCommandQueue;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
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
import java.util.Map;

@Component
public class SolicitacaoProducer {
    private static final Logger log = LoggerFactory.getLogger(SolicitacaoProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    public SolicitacaoProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) { 
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper; 
    }
    
    public void enviarParaAnalise(RegistrarSolicitacaoRequest solicitacaoDto) {
        String COMMAND_TYPE = GerenteCommandQueue.AVALIAR_SOLICITACAO.commandType();
        String QUEUE_NAME = GerenteCommandQueue.AVALIAR_SOLICITACAO.queueName();
        
        log.info("Iniciando envio da solicitacao para o ms-gerente. Fila: {} - commandType: {}", QUEUE_NAME, COMMAND_TYPE);

        JsonNode payload = objectMapper.valueToTree(solicitacaoDto);
        String timestampString = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();

        SagaCommand command = new SagaCommand(
            UUID.randomUUID().toString(),
            COMMAND_TYPE,
            timestampString,
            payload
        );
        
        rabbitTemplate.convertAndSend(QUEUE_NAME, command);
        log.info("Enviando command: {}", command);
        log.info("Enviando para fila: {}", QUEUE_NAME);
        log.info("Enviando commandType: {}", COMMAND_TYPE);
    }

    public void enviarEmailRejeicao(String email, String motivo) {
        String COMMAND_TYPE = EmailCommandQueue.REJEITAR_SOLICITACAO.commandType();
        String QUEUE_NAME = EmailCommandQueue.REJEITAR_SOLICITACAO.queueName();

        log.info("Iniciando envio da solicitacao para o ms-email. Fila: {} - commandType: {}", QUEUE_NAME, COMMAND_TYPE);

        try {
            java.util.Map<String, String> dadosEmail = java.util.Map.of(
                "email", email,
                "assunto", "Atualização sobre sua solicitação de conta",
                "mensagem", "Infelizmente sua solicitação foi recusada. Motivo: " + motivo
            );

            JsonNode payload = objectMapper.valueToTree(dadosEmail);
            String timestampString = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        
            SagaCommand command = new SagaCommand(
                UUID.randomUUID().toString(),
                COMMAND_TYPE,
                timestampString,
                payload
            );

            rabbitTemplate.convertAndSend(QUEUE_NAME, command);
            log.info("Enviando command: {}", command);
            log.info("Enviando para fila: {}", QUEUE_NAME);
            log.info("Enviando commandType: {}", COMMAND_TYPE);
        } catch (Exception e) {
            log.error("Erro ao enviar comando de e-mail para a fila {}", QUEUE_NAME, e);
        }
    }
}
