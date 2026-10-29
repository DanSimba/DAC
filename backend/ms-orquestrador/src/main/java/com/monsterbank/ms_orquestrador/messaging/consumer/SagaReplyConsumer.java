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

    @RabbitListener(queues = RabbitConfig.GERENTE_LISTAR_ATIVOS_REPLY_QUEUE)
    public void receberListagemGerentes(SagaReply reply) {
        sagaService.processarResposta("gerente.listar-ativos", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_IDENTIFICAR_GERENTE_REPLY_QUEUE)
    public void receberGerenteIdentificado(SagaReply reply) {
        sagaService.processarResposta("conta.identificar-gerente", reply);
    }

    @RabbitListener(queues = RabbitConfig.AUTH_CRIAR_USUARIO_REPLY_QUEUE)
    public void receberCriacaoUsuario(SagaReply reply) {
        sagaService.processarResposta("auth.criar-usuario", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_CRIAR_REPLY_QUEUE)
    public void receberCriacaoConta(SagaReply reply) {
        sagaService.processarResposta("conta.criar", reply);
    }
}
