package com.monsterbank.ms_email.mensageria;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_email.enumeration.EmailReplyQueue;
import com.monsterbank.ms_email.mensageria.dto.SagaCommand;
import com.monsterbank.ms_email.service.EmailService;

import tools.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EmailCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(EmailCommandConsumer.class);

    private final EmailService emailService;

    public EmailCommandConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "ms.email.cmd")
    public void receber(SagaCommand command) {
        EmailReplyQueue replyQueue = EmailReplyQueue.fromCommandType(command.tipo()).orElse(EmailReplyQueue.COMANDO_DESCONHECIDO);

        log.info("Recebimento da command {}", command);

        try {
            switch (replyQueue) {
                case REJEITAR_SOLICITACAO:
                    log.info("Executando comando REJEITAR_SOLICITACAO para SagaID: {}", command.sagaId());

                    String email = command.payload().get("email").asString();
                    String assunto = command.payload().get("assunto").asString();
                    String mensagem = command.payload().get("mensagem").asString();

                    log.info("Enviando email: {}", email);
                    log.info("Enviando assunto: {}", assunto);
                    log.info("Enviando mensagem: {}", mensagem);

                    emailService.sendEmailText(email, assunto, mensagem);
                    break;
            
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("Erro ao executar comando {}", command.tipo(), e);
        }
    }

}
