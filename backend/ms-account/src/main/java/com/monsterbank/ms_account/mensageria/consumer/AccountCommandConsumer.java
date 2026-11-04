package com.monsterbank.ms_account.mensageria.consumer;

import com.monsterbank.ms_account.exceptions.*;
import com.monsterbank.ms_account.mensageria.dto.SagaCommand;
import com.monsterbank.ms_account.mensageria.dto.SagaReply;
import com.monsterbank.ms_account.mensageria.enumeration.AccountReplyQueue;
import com.monsterbank.ms_account.mensageria.producer.OrquestradorResponseProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AccountCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(AccountCommandConsumer.class);

    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;

    public AccountCommandConsumer(

            ObjectMapper objectMapper,
            OrquestradorResponseProducer responseProducer
    ) {

        this.objectMapper = objectMapper;
        this.responseProducer = responseProducer;
    }

    @RabbitListener(queues = "ms.conta.cmd")
    public void receber(SagaCommand command){
        AccountReplyQueue replyQueue = AccountReplyQueue.fromCommandType(command.tipo())
                .orElse(AccountReplyQueue.COMANDO_DESCONHECIDO);
        
        log.info("{} --- Iniciando consumo do ms.conta.cmd", AccountCommandConsumer.class);

        try{
            switch (command.tipo()){
                //Fazer os cases do que pode chegar no ms.conta
               
            }
        } catch(Exception e){
            //Fazer as exceptions
        }   


    }

    private void enviarRespostaSucesso(
            SagaCommand command,
            AccountReplyQueue replyQueue,
            JsonNode payload
    ){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                "SUCESSO",
                null,
                payload

        );

        log.info("{} --- Enviando resposta: {}", AccountCommandConsumer.class, reply);

        responseProducer.enviar(replyQueue, reply);
    }

    private void enviarRespostaErro(
            SagaCommand command,
            AccountReplyQueue replyQueue,
            String erro
    ){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                "FALHA",
                erro,
                objectMapper.createObjectNode()

        );

        responseProducer.enviar(replyQueue, reply);
    }

}
