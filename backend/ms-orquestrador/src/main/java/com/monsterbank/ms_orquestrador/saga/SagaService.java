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
    public SagaService(ClienteCommandProducer clienteCommandProducer, SagaStateRepository sagaStateRepository) {
        this.clienteCommandProducer = clienteCommandProducer;
        this.sagaStateRepository = sagaStateRepository;
    }

    public void iniciar(SagaCommand command) {
        validarInicio(command);

        if (sagaStateRepository.existe(command.sagaId())) {
            return;
        }

        SagaState sagaState = new SagaState(
            command.sagaId(),
            APROVAR_CLIENTE,
            command.payload(),
            APROVAR_SOLICITACAO,
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
        SagaState state = sagaStateRepository.buscarPorId(reply.sagaId());
        if (state == null) {
            log.warn("Resposta da saga {} ignorada: estado inexistente", reply.sagaId());
            return;
        }
        if (state.finalizada()) {
            log.info("Resposta da saga {} ignorada: saga ja finalizada", reply.sagaId());
            return;
        }
        if (!operacao.equals(state.operacaoAtual())) {
            log.warn("Saga {} aguardava {}, mas recebeu {}", reply.sagaId(), state.operacaoAtual(), operacao);
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
            sagaStateRepository.salvar(state);
            return;
        }
        state.avancarPara(CRIAR_CLIENTE, 2);
        sagaStateRepository.salvar(state);
        enviar(state, CRIAR_CLIENTE);
    }

    private void processarCriacao(SagaState state, SagaReply reply) {
        if (reply.sucesso()) {
            state.concluir();
            sagaStateRepository.salvar(state);
            log.info("Saga {} concluiu a parte do MS Cliente", state.sagaId());
            return;
        }
        state.compensar(COMPENSAR_APROVACAO, 3, reply.erro());
        sagaStateRepository.salvar(state);
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
        sagaStateRepository.salvar(state);
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
