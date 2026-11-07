package com.monsterbank.ms_account.mensageria.consumer;

import com.monsterbank.ms_account.Account.AccountService;
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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AccountCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(AccountCommandConsumer.class);

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;

    public AccountCommandConsumer(
            AccountService accountService,
            ObjectMapper objectMapper,
            OrquestradorResponseProducer responseProducer
    ) {
        this.accountService = accountService;
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
                case "conta.identificar-gerente" -> {
                    log.info("{} --- Fila conta.identificar-gerente", AccountCommandConsumer.class);

                    JsonNode listaGerentesNode = command.payload();

                    List<Long> idsGerentes = new ArrayList<>();

                    if (listaGerentesNode != null && listaGerentesNode.isArray()) {
                        log.info("{} --- Recebido array diferente de null", AccountCommandConsumer.class);
                        int contador = 1;
                        for (JsonNode node : listaGerentesNode) {
                            Long id = node.get("id").asLong();
                            idsGerentes.add(id);
                            log.info("{} --- Contador: {} - ID: {}", AccountCommandConsumer.class, contador, id);
                            contador++;
                        }
                    }

                    log.info("{} --- Buscando gerentes com menos contas vinculadas", AccountCommandConsumer.class);
                    Long idGerenteMenosOcupado = accountService.findLeastBusyManager(idsGerentes);
                    JsonNode payload = objectMapper.valueToTree(idGerenteMenosOcupado);

                    log.info("{} --- Enviando resposta de sucesso com o id de gerente: {}", AccountCommandConsumer.class, idGerenteMenosOcupado);
                    enviarRespostaSucesso(command, replyQueue, payload);
                }

                default -> {
                    log.warn("{} --- Erro: COMANDO_DESCONHECIDO", AccountCommandConsumer.class);
                    enviarRespostaErro(command, replyQueue, null);
                }
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
