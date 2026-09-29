package com.monsterbank.ms_orquestrador.messaging.consumer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.saga.SagaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SagaReplyConsumer {

    private final SagaService sagaService;

    public SagaReplyConsumer(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_APROVAR_SOLICITACAO_REPLY_QUEUE)
    public void receberAprovacaoSolicitacao(SagaReply reply) {
        sagaService.processarResposta("cliente.aprovar-solicitacao", reply);
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_CRIAR_REPLY_QUEUE)
    public void receberCriacaoCliente(SagaReply reply) {
        sagaService.processarResposta("cliente.criar", reply);
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_COMPENSAR_APROVACAO_REPLY_QUEUE)
    public void receberCompensacaoAprovacao(SagaReply reply) {
        sagaService.processarResposta("cliente.compensar-aprovacao", reply);
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_MARCAR_NAO_APROVADA_REPLY_QUEUE)
    public void receberMarcacaoNaoAprovada(SagaReply reply) {
        sagaService.processarResposta("cliente.marcar-nao-aprovada", reply);
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_COMPENSAR_CRIACAO_REPLY_QUEUE)
    public void receberCompensacaoCriacao(SagaReply reply) {
        sagaService.processarResposta("cliente.compensar-criacao", reply);
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_COMANDO_DESCONHECIDO_REPLY_QUEUE)
    public void receberComandoDesconhecido(SagaReply reply) {
        sagaService.processarResposta("cliente.comando-desconhecido", reply);
    }
}
