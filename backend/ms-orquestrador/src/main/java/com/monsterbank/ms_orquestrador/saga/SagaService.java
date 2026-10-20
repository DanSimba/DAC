package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import com.monsterbank.ms_orquestrador.redis.SagaStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SagaService {

    private final SagaStateRepository sagaStateRepository;
    public static final String APROVAR_CLIENTE = "aprovar-cliente";
    public static final String APROVAR_SOLICITACAO = "cliente.aprovar-solicitacao";
    public static final String CRIAR_CLIENTE = "cliente.criar";
    public static final String COMPENSAR_APROVACAO = "cliente.compensar-aprovacao";

    private static final Logger log = LoggerFactory.getLogger(SagaService.class);
    private final ClienteCommandProducer clienteCommandProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, SagaState> sagas = new ConcurrentHashMap<>();

    public SagaService(ClienteCommandProducer clienteCommandProducer, SagaStateRepository sagaStateRepository) {
        this.clienteCommandProducer = clienteCommandProducer;
        this.sagaStateRepository = sagaStateRepository;
    }

    // Comentado para testar fazer com que o próprio orquestrador mantenha a SAGA, sem armazenar em memória
    // public void iniciar(SagaCommand command) {
    //     validarInicio(command);
    //     String cpf = command.payload().get("cpf").asString();
    //     SagaState state = new SagaState(command.sagaId(), cpf, command.payload(), APROVAR_SOLICITACAO);

    //     if (sagas.putIfAbsent(command.sagaId(), state) != null) {
    //         log.info("Saga {} ja iniciada; comando duplicado ignorado", command.sagaId());
    //         return;
    //     }
    //     enviar(state, APROVAR_SOLICITACAO);
    // }


    public void iniciar(SagaCommand command) {
        validarInicio(command);

        if (sagaStateRepository.existe(command.sagaId())) {
            return;
        }

        SagaState sagaState = new SagaState(
            command.sagaId(),
            APROVAR_SOLICITACAO,
            command.payload(),
            1
        );

        sagaStateRepository.salvar(sagaState);
        
        enviar(sagaState, APROVAR_SOLICITACAO);
    }

    /**
     * Ponto unico de entrada das respostas. A continuacao/compensacao da saga pode ser
     * acrescentada aqui sem acoplar os listeners aos demais microsservicos.
     */
    public void processarResposta(String operacao, SagaReply reply) {
        SagaState state = sagas.get(reply.sagaId());
        if (state == null) {
            log.warn("Resposta da saga {} ignorada: estado inexistente", reply.sagaId());
            return;
        }
        if (!state.etapaAtual().equals(operacao)) {
            log.warn("Saga {} aguardava {}, mas recebeu {}", reply.sagaId(), state.etapaAtual(), operacao);
            return;
        }

        switch (operacao) {
            case APROVAR_SOLICITACAO -> processarAprovacao(state, reply);
            case CRIAR_CLIENTE -> processarCriacao(state, reply);
            case COMPENSAR_APROVACAO -> finalizarCompensacao(state, reply);
            default -> log.warn("Operacao {} nao pertence a esta etapa da saga", operacao);
        }
    }

    private void processarAprovacao(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) {
            state.falhar(reply.erro());
            sagas.remove(state.sagaId());
            return;
        }
        state.avancarPara(CRIAR_CLIENTE);
        enviar(state, CRIAR_CLIENTE);
    }

    private void processarCriacao(SagaState state, SagaReply reply) {
        if (reply.sucesso()) {
            state.concluir();
            sagas.remove(state.sagaId());
            log.info("Saga {} concluiu a parte do MS Cliente", state.sagaId());
            return;
        }
        state.compensar(COMPENSAR_APROVACAO, reply.erro());
        enviar(state, COMPENSAR_APROVACAO);
    }

    private void finalizarCompensacao(SagaState state, SagaReply reply) {
        if (reply.sucesso()) {
            state.falhar(state.erro());
            log.info("Saga {} compensada no MS Cliente", state.sagaId());
        } else {
            state.falhar("Falha ao compensar aprovacao: " + reply.erro());
            log.error("Saga {} falhou durante a compensacao", state.sagaId());
        }
        sagas.remove(state.sagaId());
    }

    private void enviar(SagaState state, String tipo) {
        JsonNode payload = objectMapper.createObjectNode().put("cpf", state.cpf());
        clienteCommandProducer.enviar(state.sagaId(), tipo, payload);
    }

    private void validarInicio(SagaCommand command) {
        if (command == null || !APROVAR_CLIENTE.equals(command.tipo()))
            throw new IllegalArgumentException("tipo de saga nao suportado");
        if (command.payload() == null || command.payload().get("cpf") == null
                || command.payload().get("cpf").asString().isBlank())
            throw new IllegalArgumentException("cpf e obrigatorio para aprovar cliente");
    }
}
