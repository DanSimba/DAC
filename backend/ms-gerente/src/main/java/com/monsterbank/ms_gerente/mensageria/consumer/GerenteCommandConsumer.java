package com.monsterbank.ms_gerente.mensageria.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.monsterbank.ms_gerente.gerente.GerenteDTO;
import com.monsterbank.ms_gerente.gerente.GerenteService;
import com.monsterbank.ms_gerente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_gerente.mensageria.dto.SagaReply;
import com.monsterbank.ms_gerente.mensageria.enumeration.GerenteReplyQueue;
import com.monsterbank.ms_gerente.mensageria.publisher.OrquestradorResponseProducer;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component 
public class GerenteCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(GerenteCommandConsumer.class);
    
    private final GerenteService gerenteService;
    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;

    public GerenteCommandConsumer(
        GerenteService gerenteService,
        ObjectMapper objectMapper,
        OrquestradorResponseProducer responseProducer
    ) {
        this.gerenteService = gerenteService;
        this.objectMapper = objectMapper;
        this.responseProducer = responseProducer;
    }

    @RabbitListener(queues = "ms.gerente.cmd")
    public void receber(SagaCommand command) {
        GerenteReplyQueue replyQueue = GerenteReplyQueue.fromCommandType(command.tipo())
            .orElse(GerenteReplyQueue.COMANDO_DESCONHECIDO);

        log.info("{} --- Iniciando consumo do ms.gerente.cmd", GerenteCommandConsumer.class);

        try {
            switch (command.tipo()) {
                case "gerente.listar-ativos" -> {
                    log.info("{} --- Fila gerente.listar-ativos", GerenteCommandConsumer.class);

                    log.info("{} --- Buscando gerentes ativos", GerenteCommandConsumer.class);
                    List<GerenteDTO> listaGerentesAtivos = gerenteService.listar();

                    JsonNode payload = objectMapper.valueToTree(listaGerentesAtivos);

                    log.info("{} --- Enviando resposta de sucesso para a command: {}", GerenteCommandConsumer.class, command);
                    enviarRespostaSucesso(command, replyQueue, payload);

                }
            
                default -> {
                    log.warn("{} --- Erro: COMANDO_DESCONHECIDO", GerenteCommandConsumer.class);
                }
            }
        } catch (Exception e) {
            log.error("{} --- Erro crítico ao processar ms.gerente.cmd", GerenteCommandConsumer.class, e);
            enviarRespostaErro(command, replyQueue, "ERRO_INTERNO_MS_GERENTE: " + e.getMessage());
        }
    }

    private void enviarRespostaSucesso(SagaCommand command, GerenteReplyQueue replyQueue, JsonNode payload) {
        SagaReply reply = new SagaReply(
            command.sagaId(),
            LocalDateTime.now().toString(),
            "SUCESSO",
            null,
            payload
        );

        log.info("{} --- Enviando resposta: {}", GerenteCommandConsumer.class, reply);
        responseProducer.enviar(replyQueue, reply);
    }

    private void enviarRespostaErro(SagaCommand command, GerenteReplyQueue replyQueue, String mensagemErro) {
        SagaReply reply = new SagaReply(
            command.sagaId(),
            LocalDateTime.now().toString(),
            "ERRO",
            mensagemErro,
            objectMapper.createObjectNode() // Manda um JSON vazio para não dar NullPointer no Orquestrador
        );

        log.info("{} --- Enviando resposta de ERRO: {}", GerenteCommandConsumer.class, reply);
        responseProducer.enviar(replyQueue, reply);
    }

}
