package com.monsterbank.ms_cliente.mensageria.producer;

import com.monsterbank.ms_cliente.mensageria.dto.SagaReply;
import com.monsterbank.ms_cliente.mensageria.enumeration.ClienteReplyQueue;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrquestradorResponseProducerTest {

    @Test
    void devePublicarNaFilaQueIdentificaAOperacao() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        OrquestradorResponseProducer producer = new OrquestradorResponseProducer(rabbitTemplate);
        SagaReply reply = new SagaReply(
                "saga-1",
                "2026-09-22T18:00:00",
                "SUCESSO",
                null,
                new ObjectMapper().createObjectNode()
        );

        producer.enviar(ClienteReplyQueue.CRIAR, reply);

        verify(rabbitTemplate).convertAndSend("ms.cliente.criar.reply", reply);
    }
}
