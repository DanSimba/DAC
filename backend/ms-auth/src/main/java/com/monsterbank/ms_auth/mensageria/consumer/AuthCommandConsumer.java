package com.monsterbank.ms_auth.mensageria.consumer;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.monsterbank.ms_auth.auth.AuthService;
import com.monsterbank.ms_auth.enumeration.AuthReplyQueue;
import com.monsterbank.ms_auth.exception.EmailJaCadastradoException;
import com.monsterbank.ms_auth.mensageria.dto.SagaCommand;
import com.monsterbank.ms_auth.mensageria.dto.SagaReply;
import com.monsterbank.ms_auth.mensageria.publisher.OrquestradorResponseProducer;

@Component 
public class AuthCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(AuthCommandConsumer.class);

    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;

    public AuthCommandConsumer(
            AuthService authService, 
            ObjectMapper objectMapper,
            OrquestradorResponseProducer responseProducer) {
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.responseProducer = responseProducer;
    }

    @RabbitListener(queues = "ms.auth.cmd")
    public void receber(SagaCommand command) {
        AuthReplyQueue replyQueue = AuthReplyQueue.fromCommandType(command.tipo())
            .orElse(AuthReplyQueue.COMANDO_DESCONHECIDO);
        
        log.info("{} --- Iniciando consumo do ms.auth.cmd", AuthCommandConsumer.class);

        try {
            switch (command.tipo()) {
                case "auth.criar-usuario" -> {
                     log.info("{} --- Fila auth.criar-usuario", AuthCommandConsumer.class);

                     String email = command.payload().get("email").asString();
                     String cpf = command.payload().get("cpf").asString();

                     log.info("{} --- Email: {}. CPF: {}", AuthCommandConsumer.class, email, cpf);

                     String senhaProvisoria = authService.criarUsuarioCliente(email, cpf);

                     log.info("{} --- Senha provisória: {}", AuthCommandConsumer.class, senhaProvisoria);

                     ObjectNode payloadResposta = objectMapper.createObjectNode();
                     payloadResposta.put("senhaTemporariaTransacional", senhaProvisoria);

                     log.info("{} --- Enviando resposta de sucesso para a command: {}", AuthCommandConsumer.class, command);
                     enviarRespostaSucesso(command, replyQueue, payloadResposta);
                } 
            
                default -> {
                    log.warn("{} --- Erro: COMANDO_DESCONHECIDO", AuthCommandConsumer.class);
                }
            }
        } catch (EmailJaCadastradoException e) {
            log.warn("{} --- Erro de negócio no ms-auth: {}", AuthCommandConsumer.class, e.getMessage());
            enviarRespostaErro(command, replyQueue, e.getMessage());
        } catch (Exception e) {
            log.error("{} --- Erro interno catastrófico no ms-auth", AuthCommandConsumer.class, e);
            enviarRespostaErro(command, replyQueue, "Erro interno ao processar credenciais no ms-auth");
        }
    }

    private void enviarRespostaSucesso(
            SagaCommand command,
            AuthReplyQueue replyQueue,
            JsonNode payload
    ){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                "SUCESSO",
                null,
                payload

        );

        log.info("{} --- Enviando resposta: {}", AuthCommandConsumer.class, reply);

        responseProducer.enviar(replyQueue, reply);
    }

    private void enviarRespostaErro(
            SagaCommand command,
            AuthReplyQueue replyQueue,
            String erro
    ){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                "FALHA",
                erro,
                objectMapper.createObjectNode()

        );

        log.info("{} --- Enviando resposta: {}", AuthCommandConsumer.class, reply);
        responseProducer.enviar(replyQueue, reply);
    }

}
