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
        //EmailReplyQueue replyQueue = EmailReplyQueue.fromCommandType(command.tipo()).orElse(EmailReplyQueue.COMANDO_DESCONHECIDO);

        log.info("{} --- Iniciando consumo do ms.email.cmd", EmailReplyQueue.class);
        log.info("{} --- command: {}", EmailReplyQueue.class, command);

        try {
            switch (command.tipo()) {
                case "email.rejeitar-solicitacao" -> {
                    log.info("{} --- Fila email.rejeitar-solicitacao", EmailReplyQueue.class);

                    String email = command.payload().get("email").asString();
                    String assunto = command.payload().get("assunto").asString();
                    String mensagem = command.payload().get("mensagem").asString();

                    log.info("{} --- Email: {}", EmailCommandConsumer.class, email);
                    log.info("{} --- Assunto: {}", EmailCommandConsumer.class, assunto);
                    log.info("{} --- Mensagem: {}", EmailCommandConsumer.class, mensagem);

                    emailService.sendEmailText(email, assunto, mensagem);
                }

                case "email.enviar-senha" -> {
                    log.info("{} --- Fila email.enviar-senha", EmailReplyQueue.class);

                    String email = command.payload().get("email").asString();
                    String senha = command.payload().get("senha").asString();

                    log.info("{} --- Email: {}", EmailCommandConsumer.class, email);
                    log.info("{} --- Senha: {}", EmailCommandConsumer.class, senha);

                    emailService.sendPassword(email, senha);
                }
            
                default -> {
                    log.warn("{} --- Nenhuma fila encontrada. ", EmailCommandConsumer.class);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao executar comando {}", command.tipo(), e);
        }
    }

}
