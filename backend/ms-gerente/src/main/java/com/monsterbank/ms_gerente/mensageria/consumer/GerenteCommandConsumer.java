package com.monsterbank.ms_gerente.mensageria.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.monsterbank.ms_gerente.gerente.GerenteDTO;
import com.monsterbank.ms_gerente.gerente.GerenteService;
import com.monsterbank.ms_gerente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_gerente.mensageria.enumeration.GerenteReplyQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component 
public class GerenteCommandConsumer {
    private static final Logger log = LoggerFactory.getLogger(GerenteCommandConsumer.class);

    private final GerenteService gerenteService;

    public GerenteCommandConsumer(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }

    @RabbitListener(queues = "ms.gerente.cmd")
    public void receber(SagaCommand command) {
            GerenteReplyQueue replyQueue = GerenteReplyQueue.fromCommandType(command.tipo()).orElse(GerenteReplyQueue.COMANDO_DESCONHECIDO);
        
        log.info("Recebimento da command {}", command);

            try {
                switch (replyQueue) {
                    case AVALIAR_SOLICITACAO:
                        log.info("Executando comando AVALIAR_SOLICITACAO para SagaID: {}", command.sagaId());
                        
                        String nome = command.payload().get("nome").asString();
                        String cpf = command.payload().get("cpf").asString();
                        String salario = command.payload().get("salario").asString();

                        log.info("Enviando nome: {}", nome);
                        log.info("Enviando cpf: {}", cpf);
                        log.info("Enviando salario: {}", salario);
                        gerenteService.registraAvaliacaoPendente(cpf, nome, salario);

                        break;
                
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("Erro ao executar comando {}", command.tipo(), e);
            }
    }

}
