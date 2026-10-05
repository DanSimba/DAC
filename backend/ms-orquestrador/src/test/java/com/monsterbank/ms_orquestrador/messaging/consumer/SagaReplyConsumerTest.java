package com.monsterbank.ms_orquestrador.messaging.consumer;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.saga.SagaService;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaReplyConsumerTest {

    @Test
    void deveIdentificarOperacaoPelaFilaDeResposta() {
        SagaService sagaService = mock(SagaService.class);
        SagaReplyConsumer consumer = new SagaReplyConsumer(sagaService);
        SagaReply reply = new SagaReply(
                "saga-1",
                "2026-09-29T01:00:00",
                "SUCESSO",
                null,
                new ObjectMapper().createObjectNode()
        );

        consumer.receberAprovacaoSolicitacao(reply);
        consumer.receberCriacaoCliente(reply);
        consumer.receberCompensacaoAprovacao(reply);
        consumer.receberMarcacaoNaoAprovada(reply);
        consumer.receberCompensacaoCriacao(reply);
        consumer.receberComandoDesconhecido(reply);

        verify(sagaService).processarResposta("cliente.aprovar-solicitacao", reply);
        verify(sagaService).processarResposta("cliente.criar", reply);
        verify(sagaService).processarResposta("cliente.compensar-aprovacao", reply);
        verify(sagaService).processarResposta("cliente.marcar-nao-aprovada", reply);
        verify(sagaService).processarResposta("cliente.compensar-criacao", reply);
        verify(sagaService).processarResposta("cliente.comando-desconhecido", reply);
    }
}
