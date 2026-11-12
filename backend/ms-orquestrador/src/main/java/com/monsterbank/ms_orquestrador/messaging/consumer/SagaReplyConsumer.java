package com.monsterbank.ms_orquestrador.messaging.consumer;

import com.monsterbank.ms_orquestrador.config.RabbitConfig;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.saga.SagaService;
import com.monsterbank.ms_orquestrador.saga.SagaState;
import com.monsterbank.ms_orquestrador.redis.SagaStateRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SagaReplyConsumer {
    private static final Logger log = LoggerFactory.getLogger(SagaReplyConsumer.class);

    private final SagaService sagaService;
    private final SagaStateRepository sagaStateRepository;

    public SagaReplyConsumer(SagaService sagaService, SagaStateRepository sagaStateRepository) {
        this.sagaService = sagaService;
        this.sagaStateRepository = sagaStateRepository;
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

    @RabbitListener(queues = RabbitConfig.CONTA_APROVAR_SOLICITACAO_REPLY_QUEUE)
    public void receberAprovarSolicitacaoConta(SagaReply reply) {
        sagaService.processarResposta("conta.aprovar-solicitacao", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_IDENTIFICAR_GERENTE_REPLY_QUEUE)
    public void receberIdentificarGerente(SagaReply reply) {
        sagaService.processarResposta("conta.identificar-gerente", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_COMPENSAR_APROVACAO_REPLY_QUEUE)
    public void receberCompensacaoAprovacaoConta(SagaReply reply) {
        sagaService.processarResposta("conta.compensar-aprovacao", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_MARCAR_NAO_APROVADA_REPLY_QUEUE)
    public void receberMarcarNaoAprovadaConta(SagaReply reply) {
        sagaService.processarResposta("conta.marcar-nao-aprovada", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_COMPENSAR_CRIACAO_REPLY_QUEUE)
    public void receberCompensacaoCriacaoConta(SagaReply reply) {
        sagaService.processarResposta("conta.compensar-criacao", reply);
    }

    @RabbitListener(queues = RabbitConfig.CONTA_COMANDO_DESCONHECIDO_REPLY_QUEUE)
    public void receberComandoDesconhecidoConta(SagaReply reply) {
        // Opcional: Como o tipo é nulo no Enum, passamos uma string de fallback pro SagaService
        sagaService.processarResposta("conta.comando-desconhecido", reply);
    }
}
