package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SagaServiceTest {

    private final ClienteCommandProducer clienteCommandProducer = mock(ClienteCommandProducer.class);
    private final SagaService sagaService = new SagaService(clienteCommandProducer);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarPrimeiroComandoDaSagaAprovarCliente() {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        SagaCommand startCommand = new SagaCommand(
                "saga-1",
                SagaService.APROVAR_CLIENTE,
                "2026-10-06T13:45:12",
                payload
        );

        sagaService.iniciar(startCommand);

        verify(clienteCommandProducer).enviar(
                "saga-1",
                SagaService.APROVAR_SOLICITACAO,
                payload
        );
    }

    @Test
    void deveRejeitarSagaSemCpf() {
        SagaCommand startCommand = new SagaCommand(
                "saga-1",
                SagaService.APROVAR_CLIENTE,
                "2026-10-06T13:45:12",
                objectMapper.createObjectNode()
        );

        assertThatThrownBy(() -> sagaService.iniciar(startCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cpf e obrigatorio para aprovar cliente");
    }

    @Test
    void deveCriarClienteDepoisDaAprovacao() {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        sagaService.iniciar(new SagaCommand("saga-2", SagaService.APROVAR_CLIENTE, "agora", payload));

        sagaService.processarResposta(
                SagaService.APROVAR_SOLICITACAO,
                new SagaReply("saga-2", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        verify(clienteCommandProducer).enviar("saga-2", SagaService.CRIAR_CLIENTE, payload);
    }

    @Test
    void deveCompensarAprovacaoQuandoCriacaoFalhar() {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        sagaService.iniciar(new SagaCommand("saga-3", SagaService.APROVAR_CLIENTE, "agora", payload));
        sagaService.processarResposta(
                SagaService.APROVAR_SOLICITACAO,
                new SagaReply("saga-3", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        sagaService.processarResposta(
                SagaService.CRIAR_CLIENTE,
                new SagaReply("saga-3", "agora", "FALHA", "erro", objectMapper.createObjectNode())
        );

        verify(clienteCommandProducer).enviar("saga-3", SagaService.COMPENSAR_APROVACAO, payload);
    }
}
