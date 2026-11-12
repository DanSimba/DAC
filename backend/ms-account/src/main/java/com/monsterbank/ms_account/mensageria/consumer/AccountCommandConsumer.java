package com.monsterbank.ms_account.mensageria.consumer;

import com.monsterbank.ms_account.account.AccountService;
import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.exceptions.*;
import com.monsterbank.ms_account.mensageria.dto.SagaCommand;
import com.monsterbank.ms_account.mensageria.dto.SagaReply;
import com.monsterbank.ms_account.mensageria.enumeration.AccountReplyQueue;
import com.monsterbank.ms_account.mensageria.producer.OrquestradorResponseProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

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

                    ObjectNode payload = objectMapper.createObjectNode();
                    payload.put("gerenteId", idGerenteMenosOcupado);

                    log.info("{} --- Enviando resposta de sucesso com o id de gerente: {}", AccountCommandConsumer.class, idGerenteMenosOcupado);
                    enviarRespostaSucesso(command, replyQueue, payload);
                }

                case "conta.criar" -> {
                    log.info("{} --- Fila conta.identificar-gerente", AccountCommandConsumer.class);

                    String cpf = command.payload().get("cpf").asString();
                    Long managerId = command.payload().get("gerenteId").asLong();

                    AccountDTO account = accountService.createAccount(cpf, managerId);

                    JsonNode payload = objectMapper.valueToTree(account);
                    log.info("{} --- Enviando resposta de sucesso com dados da conta. Payload: {}", AccountCommandConsumer.class, payload);
                    enviarRespostaSucesso(command, replyQueue, payload);

                }

                default -> {
                    log.warn("{} --- Erro: COMANDO_DESCONHECIDO", AccountCommandConsumer.class);
                    enviarRespostaErro(command, replyQueue, null);
                }
            }
        } catch (ErroCriacaoAccountException | ErroListaDeGerenteVaziaOuNula e) {
            log.warn("{} --- Erro de negócio ao processar comando [{}]: {}", AccountCommandConsumer.class, command.tipo(), e.getMessage());
            enviarRespostaErro(command, replyQueue, e.getMessage());

        } catch (MalabarismoException e) {
            log.warn("{} --- MalabarismoException disparada no comando [{}]: {}", AccountCommandConsumer.class, command.tipo(), e.getMessage());
            enviarRespostaErro(command, replyQueue, e.getMessage());

        } catch (Exception e) {
            log.error("{} --- Erro catastrófico não mapeado no comando [{}]. Detalhes: ", AccountCommandConsumer.class, command.tipo(), e);
            enviarRespostaErro(command, replyQueue, "Erro interno inesperado no serviço de contas (ms-account).");
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

        log.info("{} --- Enviando resposta: {}, Fila: {}", AccountCommandConsumer.class, reply, replyQueue);
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
