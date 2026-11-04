package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.messaging.producer.AuthCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.ContaCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.EmailCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.GerenteCommandProducer;
import com.monsterbank.ms_orquestrador.redis.SagaStateRepository;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class SagaServiceTest {

    private final ClienteCommandProducer clienteCommandProducer = mock(ClienteCommandProducer.class);
    private final SagaStateRepository sagaStateRepository = mock(SagaStateRepository.class);
    private final GerenteCommandProducer gerenteCommandProducer = mock(GerenteCommandProducer.class);
    private final ContaCommandProducer contaCommandProducer = mock(ContaCommandProducer.class);
    private final AuthCommandProducer authCommandProducer = mock(AuthCommandProducer.class);
    private final EmailCommandProducer emailCommandProducer = mock(EmailCommandProducer.class);
    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    
    //private final SagaService sagaService = new SagaService(clienteCommandProducer, sagaStateRepository);
    
    private final SagaService sagaService = new SagaService(
            clienteCommandProducer,
            gerenteCommandProducer,
            contaCommandProducer,
            authCommandProducer,
            emailCommandProducer,
            sagaStateRepository,
            stringRedisTemplate
    );
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, SagaState> estados = new HashMap<>();

    @BeforeEach
    void configurarRepositorioRedis() {
        estados.clear();
        when(sagaStateRepository.existe(anyString()))
                .thenAnswer(invocation -> estados.containsKey(invocation.getArgument(0)));
        when(sagaStateRepository.buscarPorId(anyString()))
                .thenAnswer(invocation -> estados.get(invocation.getArgument(0)));
        doAnswer(invocation -> {
            SagaState state = invocation.getArgument(0);
            estados.put(state.sagaId(), state);
            return null;
        }).when(sagaStateRepository).salvar(any(SagaState.class));
    }

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
    void deveListarGerentesDepoisDaAprovacao() {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        sagaService.iniciar(new SagaCommand("saga-2", SagaService.APROVAR_CLIENTE, "agora", payload));

        sagaService.processarResposta(
                SagaService.APROVAR_SOLICITACAO,
                new SagaReply("saga-2", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        verify(gerenteCommandProducer).enviar(
                "saga-2",
                SagaService.LISTAR_GERENTES,
                objectMapper.createObjectNode()
        );
    }

    @Test
    void deveAguardarRespostaDaCompensacaoAntesDeFinalizarSaga() {
        avancarAteCriacaoCliente("saga-3");

        sagaService.processarResposta(
                SagaService.CRIAR_CLIENTE,
                new SagaReply("saga-3", "agora", "FALHA", "erro", objectMapper.createObjectNode())
        );

        assertThat(estados.get("saga-3").getStatus()).isEqualTo(SagaState.Status.COMPENSANDO);
        verify(clienteCommandProducer).enviar(
                "saga-3",
                SagaService.COMPENSAR_APROVACAO,
                objectMapper.createObjectNode().put("cpf", "12345678901")
        );

        sagaService.processarResposta(
                SagaService.COMPENSAR_APROVACAO,
                new SagaReply("saga-3", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        assertThat(estados.get("saga-3").getStatus()).isEqualTo(SagaState.Status.FALHA);
        assertThat(estados.get("saga-3").erro()).isEqualTo("erro");
    }

    @Test
    void deveCompensarCriacaoAntesDeCompensarAprovacao() {
        avancarAteCriacaoCliente("saga-5");
        sagaService.processarResposta(
                SagaService.CRIAR_CLIENTE,
                new SagaReply("saga-5", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        sagaService.processarResposta(
                SagaService.CRIAR_USUARIO,
                new SagaReply("saga-5", "agora", "FALHA", "AUTH_INDISPONIVEL", objectMapper.createObjectNode())
        );

        verify(clienteCommandProducer).enviar(
                "saga-5",
                SagaService.COMPENSAR_CRIACAO_CLIENTE,
                objectMapper.createObjectNode().put("cpf", "12345678901")
        );
        assertThat(estados.get("saga-5").operacaoAtual())
                .isEqualTo(SagaService.COMPENSAR_CRIACAO_CLIENTE);

        sagaService.processarResposta(
                SagaService.COMPENSAR_CRIACAO_CLIENTE,
                new SagaReply("saga-5", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        verify(clienteCommandProducer).enviar(
                "saga-5",
                SagaService.COMPENSAR_APROVACAO,
                objectMapper.createObjectNode().put("cpf", "12345678901")
        );
        assertThat(estados.get("saga-5").getStatus()).isEqualTo(SagaState.Status.COMPENSANDO);
    }

    @Test
    void deveMarcarSolicitacaoNaoAprovadaDepoisDeRemoverClienteComEmailDuplicado() {
        avancarAteCriacaoCliente("saga-6");
        sagaService.processarResposta(
                SagaService.CRIAR_CLIENTE,
                new SagaReply("saga-6", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );
        sagaService.processarResposta(
                SagaService.CRIAR_USUARIO,
                new SagaReply("saga-6", "agora", "FALHA", "EMAIL_DUPLICADO", objectMapper.createObjectNode())
        );

        sagaService.processarResposta(
                SagaService.COMPENSAR_CRIACAO_CLIENTE,
                new SagaReply("saga-6", "agora", "SUCESSO", null, objectMapper.createObjectNode())
        );

        verify(clienteCommandProducer).enviar(
                "saga-6",
                SagaService.MARCAR_NAO_APROVADA,
                objectMapper.createObjectNode()
                        .put("cpf", "12345678901")
                        .put("motivo", "E-mail já cadastrado")
        );
        assertThat(estados.get("saga-6").operacaoAtual()).isEqualTo(SagaService.MARCAR_NAO_APROVADA);
    }

    @Test
    void deveRecuperarEstadoSerializadoDoRedis() throws Exception {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        SagaState original = new SagaState(
                "saga-4",
                SagaService.APROVAR_CLIENTE,
                payload,
                SagaService.APROVAR_SOLICITACAO,
                1
        );

        String json = objectMapper.writeValueAsString(original);
        SagaState recuperado = objectMapper.readValue(json, SagaState.class);

        org.assertj.core.api.Assertions.assertThat(recuperado.sagaId()).isEqualTo("saga-4");
        org.assertj.core.api.Assertions.assertThat(recuperado.cpf()).isEqualTo("12345678901");
        org.assertj.core.api.Assertions.assertThat(recuperado.operacaoAtual())
                .isEqualTo(SagaService.APROVAR_SOLICITACAO);
    }

    private void avancarAteCriacaoCliente(String sagaId) {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", "12345678901");
        sagaService.iniciar(new SagaCommand(sagaId, SagaService.APROVAR_CLIENTE, "agora", payload));
        sagaService.processarResposta(
                SagaService.APROVAR_SOLICITACAO,
                new SagaReply(
                        sagaId,
                        "agora",
                        "SUCESSO",
                        null,
                        objectMapper.createObjectNode().put("email", "cliente@teste.com")
                )
        );
        sagaService.processarResposta(
                SagaService.LISTAR_GERENTES,
                new SagaReply(sagaId, "agora", "SUCESSO", null, objectMapper.createArrayNode())
        );
        sagaService.processarResposta(
                SagaService.IDENTIFICAR_GERENTE,
                new SagaReply(
                        sagaId,
                        "agora",
                        "SUCESSO",
                        null,
                        objectMapper.createObjectNode().put("gerenteId", "gerente-1")
                )
        );
    }
}
