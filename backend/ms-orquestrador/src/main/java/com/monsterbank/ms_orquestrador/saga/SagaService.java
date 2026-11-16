package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.messaging.producer.*;
import com.monsterbank.ms_orquestrador.redis.SagaStateRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class SagaService {
    public static final String APROVAR_CLIENTE = "aprovar-cliente";
    public static final String APROVAR_SOLICITACAO = "cliente.aprovar-solicitacao";
    public static final String LISTAR_GERENTES = "gerente.listar-ativos";
    public static final String IDENTIFICAR_GERENTE = "conta.identificar-gerente";
    public static final String CRIAR_CLIENTE = "cliente.criar";
    public static final String CRIAR_CONTA = "conta.criar";
    public static final String CRIAR_USUARIO = "auth.criar-usuario";
    public static final String ENVIAR_SENHA = "email.enviar-senha";
    public static final String ENVIAR_FALHA = "email.rejeitar-solicitacao";
    public static final String COMPENSAR_CRIACAO_USUARIO = "auth.compensar-criacao";
    public static final String COMPENSAR_CRIACAO_CONTA = "conta.compensar-criacao";
    public static final String COMPENSAR_CRIACAO_CLIENTE = "cliente.compensar-criacao";
    public static final String COMPENSAR_APROVACAO = "cliente.compensar-aprovacao";
    public static final String MARCAR_NAO_APROVADA = "cliente.marcar-nao-aprovada";

    private static final Logger log = LoggerFactory.getLogger(SagaService.class);
    private static final Duration JOB_TTL = Duration.ofMinutes(5);
    private static final Duration ETAPA_TIMEOUT = Duration.ofSeconds(30);

    private final ClienteCommandProducer clienteProducer;
    private final GerenteCommandProducer gerenteProducer;
    private final ContaCommandProducer contaProducer;
    private final AuthCommandProducer authProducer;
    private final EmailCommandProducer emailProducer;
    private final SagaStateRepository stateRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, SagaReply> respostasAuthPendentes = new ConcurrentHashMap<>();

    public SagaService(ClienteCommandProducer clienteProducer, GerenteCommandProducer gerenteProducer,
            ContaCommandProducer contaProducer, AuthCommandProducer authProducer,
            EmailCommandProducer emailProducer, SagaStateRepository stateRepository,
            StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.clienteProducer = clienteProducer;
        this.gerenteProducer = gerenteProducer;
        this.contaProducer = contaProducer;
        this.authProducer = authProducer;
        this.emailProducer = emailProducer;
        this.stateRepository = stateRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void iniciar(SagaCommand command) {
        validarInicio(command);
        if (stateRepository.existe(command.sagaId())) {
            log.warn("Saga {} ja existe", command.sagaId());
            return;
        }
        SagaState state = new SagaState(command.sagaId(), APROVAR_CLIENTE,
                command.payload(), APROVAR_SOLICITACAO, 1);
        stateRepository.salvar(state);
        enviar(clienteProducer, state, APROVAR_SOLICITACAO, command.payload());
    }

    public synchronized void processarResposta(String operacao, SagaReply reply) {
        SagaState state = stateRepository.buscarPorId(reply.sagaId());
        if (state == null || state.finalizada()) return;
        if (!operacao.equals(state.operacaoAtual())) {
            log.warn("Saga {} aguardava {}, mas recebeu {}", reply.sagaId(), state.operacaoAtual(), operacao);
            return;
        }
        switch (operacao) {
            case APROVAR_SOLICITACAO -> processarAprovacao(state, reply);
            case LISTAR_GERENTES -> processarListagemGerentes(state, reply);
            case IDENTIFICAR_GERENTE -> processarIdentificacaoGerente(state, reply);
            case CRIAR_CLIENTE -> processarCriacaoCliente(state, reply);
            case CRIAR_CONTA -> processarCriacaoConta(state, reply);
            case CRIAR_USUARIO -> processarCriacaoUsuario(state, reply);
            case COMPENSAR_CRIACAO_USUARIO -> processarCompensacaoUsuario(state, reply);
            case COMPENSAR_CRIACAO_CONTA -> processarCompensacaoConta(state, reply);
            case COMPENSAR_CRIACAO_CLIENTE -> processarCompensacaoCliente(state, reply);
            case COMPENSAR_APROVACAO -> finalizarCompensacao(state, reply, "aprovacao da solicitacao");
            case MARCAR_NAO_APROVADA -> finalizarCompensacao(state, reply, "reprovacao da solicitacao");
            default -> log.warn("Operacao desconhecida na saga {}: {}", state.sagaId(), operacao);
        }
    }

    public synchronized void processarTimeout(String sagaId) {
        SagaState state = stateRepository.buscarPorId(sagaId);
        if (state == null || state.finalizada()) return;
        LocalDateTime transicao = LocalDateTime.parse(state.getTimestamp());
        if (Duration.between(transicao, LocalDateTime.now()).compareTo(ETAPA_TIMEOUT) < 0) return;

        String erro = "Timeout de 30 segundos na operacao " + state.operacaoAtual();
        if (state.getStatus() == SagaState.Status.COMPENSANDO) {
            falharSaga(state, erro + ". Erro original: " + state.erro());
            return;
        }
        switch (state.operacaoAtual()) {
            case APROVAR_SOLICITACAO, LISTAR_GERENTES, IDENTIFICAR_GERENTE -> iniciarCompensacaoAprovacao(state, erro);
            case CRIAR_CLIENTE -> iniciarCompensacaoCliente(state, erro);
            case CRIAR_CONTA -> iniciarCompensacaoConta(state, erro);
            case CRIAR_USUARIO -> iniciarCompensacaoUsuario(state, erro);
            default -> falharSaga(state, erro);
        }
    }

    private void processarAprovacao(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { falharSaga(state, reply.erro()); return; }
        ObjectNode payload = (ObjectNode) state.getPayload().deepCopy();
        payload.set("dadosSolicitacao", reply.payload());
        state.setPayload(payload);
        avancar(state, LISTAR_GERENTES, 2);
        enviar(gerenteProducer, state, LISTAR_GERENTES, objectMapper.createObjectNode());
    }

    private void processarListagemGerentes(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { iniciarCompensacaoAprovacao(state, reply.erro()); return; }
        avancar(state, IDENTIFICAR_GERENTE, 3);
        enviar(contaProducer, state, IDENTIFICAR_GERENTE, reply.payload());
    }

    private void processarIdentificacaoGerente(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { iniciarCompensacaoAprovacao(state, reply.erro()); return; }
        ObjectNode payload = (ObjectNode) state.getPayload().deepCopy();
        payload.put("gerenteCpf", reply.payload().get("gerenteCpf").asLong());
        state.setPayload(payload);
        avancar(state, CRIAR_CLIENTE, 4);
        enviar(clienteProducer, state, CRIAR_CLIENTE, state.getPayload());
    }

    private void processarCriacaoCliente(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { iniciarCompensacaoAprovacao(state, reply.erro()); return; }
        avancar(state, CRIAR_USUARIO, 5);
        String email = dadosSolicitacao(state).get("email").asString();
        enviar(authProducer, state, CRIAR_USUARIO,
                objectMapper.createObjectNode().put("cpf", state.cpf()).put("email", email));
    }

    private void processarCriacaoUsuario(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { iniciarCompensacaoUsuario(state, reply.erro()); return; }
        respostasAuthPendentes.put(state.sagaId(), reply);
        avancar(state, CRIAR_CONTA, 6);
        ObjectNode payload = objectMapper.createObjectNode().put("cpf", state.cpf())
                .put("gerenteCpf", state.getPayload().get("gerenteCpf").asLong());
        enviar(contaProducer, state, CRIAR_CONTA, payload);
    }

    private void processarCriacaoConta(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { iniciarCompensacaoConta(state, reply.erro()); return; }
        SagaReply respostaAuth = respostasAuthPendentes.remove(state.sagaId());
        if (respostaAuth == null) {
            iniciarCompensacaoConta(state, "Resposta temporaria do Auth indisponivel");
            return;
        }
        String email = dadosSolicitacao(state).get("email").asString();
        String senha = respostaAuth.payload().get("senhaTemporariaTransacional").asString();
        enviar(emailProducer, state, ENVIAR_SENHA,
                objectMapper.createObjectNode().put("email", email).put("senha", senha));
        concluirSaga(state);
    }

    private void iniciarCompensacaoUsuario(SagaState state, String erro) {
        compensar(state, COMPENSAR_CRIACAO_USUARIO, erro);
        enviar(authProducer, state, COMPENSAR_CRIACAO_USUARIO, objectMapper.createObjectNode().put("cpf", state.cpf()));
    }

    private void iniciarCompensacaoConta(SagaState state, String erro) {
        compensar(state, COMPENSAR_CRIACAO_CONTA, erro);
        enviar(contaProducer, state, COMPENSAR_CRIACAO_CONTA, objectMapper.createObjectNode().put("cpf", state.cpf()));
    }

    private void iniciarCompensacaoCliente(SagaState state, String erro) {
        compensar(state, COMPENSAR_CRIACAO_CLIENTE, erro);
        enviar(clienteProducer, state, COMPENSAR_CRIACAO_CLIENTE, objectMapper.createObjectNode().put("cpf", state.cpf()));
    }

    private void iniciarCompensacaoAprovacao(SagaState state, String erro) {
        compensar(state, COMPENSAR_APROVACAO, erro);
        enviar(clienteProducer, state, COMPENSAR_APROVACAO, objectMapper.createObjectNode().put("cpf", state.cpf()));
    }

    private void processarCompensacaoUsuario(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { finalizarCompensacao(state, reply, "criacao do usuario"); return; }
        iniciarCompensacaoCliente(state, state.erro());
    }

    private void processarCompensacaoConta(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { finalizarCompensacao(state, reply, "criacao da conta"); return; }
        iniciarCompensacaoUsuario(state, state.erro());
    }

    private void processarCompensacaoCliente(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) { finalizarCompensacao(state, reply, "criacao do cliente"); return; }
        if ("EMAIL_DUPLICADO".equals(state.erro())) {
            compensar(state, MARCAR_NAO_APROVADA, state.erro());
            ObjectNode payload = objectMapper.createObjectNode().put("cpf", state.cpf())
                    .put("motivo", "E-mail ja cadastrado");
            enviar(clienteProducer, state, MARCAR_NAO_APROVADA, payload);
            return;
        }
        iniciarCompensacaoAprovacao(state, state.erro());
    }

    private void finalizarCompensacao(SagaState state, SagaReply reply, String etapa) {
        String original = state.erro();
        if (!reply.sucesso()) {
            falharSaga(state, "Falha ao compensar " + etapa + ": " + reply.erro() + ". Erro original: " + original);
            return;
        }
        falharSaga(state, original);
    }

    private void avancar(SagaState state, String operacao, int etapa) {
        state.avancarPara(operacao, etapa);
        stateRepository.salvar(state);
    }

    private void compensar(SagaState state, String operacao, String erro) {
        state.compensar(operacao, state.getEtapaAtual(), erro);
        stateRepository.salvar(state);
    }

    private void concluirSaga(SagaState state) {
        respostasAuthPendentes.remove(state.sagaId());
        state.concluir();
        stateRepository.salvar(state);
        ObjectNode job = objectMapper.createObjectNode().put("jobId", state.sagaId())
                .put("status", "CONCLUIDO").put("resultType", "resource")
                .put("dominio", "clientes").put("resourceId", state.cpf()).putNull("erro");
        salvarJob(state.sagaId(), job);
        redisTemplate.delete("cache:cliente:" + state.cpf());
        log.info("Saga {} concluida", state.sagaId());
    }

    private void falharSaga(SagaState state, String erro) {
        respostasAuthPendentes.remove(state.sagaId());
        String mensagem = erro == null ? "Erro interno" : erro;
        state.falhar(mensagem);
        stateRepository.salvar(state);
        ObjectNode job = objectMapper.createObjectNode().put("jobId", state.sagaId())
                .put("status", "FALHA").putNull("resultType").put("dominio", "clientes")
                .putNull("resourceId").put("erro", mensagem);
        salvarJob(state.sagaId(), job);
        enviarEmailFalha(state);
        log.error("Saga {} falhou: {}", state.sagaId(), mensagem);
    }

    private void salvarJob(String sagaId, ObjectNode job) {
        try {
            redisTemplate.opsForValue().set("job:" + sagaId, objectMapper.writeValueAsString(job), JOB_TTL);
        } catch (Exception e) {
            log.error("Erro ao atualizar o job {} no Redis", sagaId, e);
        }
    }

    private void enviarEmailFalha(SagaState state) {
        JsonNode dados = dadosSolicitacao(state);
        if (dados == null || dados.get("email") == null) return;
        ObjectNode payload = objectMapper.createObjectNode().put("email", dados.get("email").asString())
                .put("assunto", "Falha na solicitacao de conta")
                .put("mensagem", "Sua solicitacao de conta nao pode ser concluida.");
        enviar(emailProducer, state, ENVIAR_FALHA, payload);
    }

    private JsonNode dadosSolicitacao(SagaState state) {
        return state.getPayload() == null ? null : state.getPayload().get("dadosSolicitacao");
    }

    private void enviar(BaseCommandProducer producer, SagaState state, String tipo, JsonNode payload) {
        log.info("Enviando comando {} da saga {} para {}", tipo, state.sagaId(), producer.getClass().getSimpleName());
        producer.enviar(state.sagaId(), tipo, payload);
    }

    private void validarInicio(SagaCommand command) {
        if (command == null || !APROVAR_CLIENTE.equals(command.tipo()))
            throw new IllegalArgumentException("tipo de saga nao suportado");
        if (command.payload() == null || command.payload().get("cpf") == null
                || command.payload().get("cpf").asString().isBlank())
            throw new IllegalArgumentException("cpf e obrigatorio para aprovar cliente");
    }
}
