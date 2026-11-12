package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.messaging.producer.AuthCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.BaseCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.ContaCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.EmailCommandProducer;
import com.monsterbank.ms_orquestrador.messaging.producer.GerenteCommandProducer;
import com.monsterbank.ms_orquestrador.redis.SagaStateRepository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class SagaService {

    private static final Logger log = LoggerFactory.getLogger(SagaService.class);
    private final StringRedisTemplate redisTemplate;
    private final SagaStateRepository sagaStateRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String LISTAR_GERENTES = "gerente.listar-ativos";
    public static final String IDENTIFICAR_GERENTE = "conta.identificar-gerente";
    public static final String CRIAR_USUARIO = "auth.criar-usuario";
    public static final String CRIAR_CONTA = "conta.criar";
    public static final String ENVIAR_SENHA = "email.enviar-senha";
    public static final String COMPENSAR_CRIACAO_CLIENTE = "cliente.compensar-criacao";
    public static final String MARCAR_NAO_APROVADA = "cliente.marcar-nao-aprovada";
    public static final String APROVAR_CLIENTE = "aprovar-cliente";
    public static final String APROVAR_SOLICITACAO = "cliente.aprovar-solicitacao";
    public static final String CRIAR_CLIENTE = "cliente.criar";
    public static final String COMPENSAR_APROVACAO = "cliente.compensar-aprovacao";


    private final GerenteCommandProducer gerenteProducer;
    private final ContaCommandProducer contaProducer;
    private final AuthCommandProducer authProducer;
    private final ClienteCommandProducer clienteCommandProducer;
    private final EmailCommandProducer emailProducer;

    public SagaService(
            ClienteCommandProducer clienteCommandProducer, GerenteCommandProducer gerenteProducer,
            ContaCommandProducer contaProducer, AuthCommandProducer authProducer,
            EmailCommandProducer emailProducer, SagaStateRepository sagaStateRepository,
            StringRedisTemplate redisTemplate) {
        this.clienteCommandProducer = clienteCommandProducer;
        this.gerenteProducer = gerenteProducer;
        this.contaProducer = contaProducer;
        this.authProducer = authProducer;
        this.emailProducer = emailProducer;
        this.sagaStateRepository = sagaStateRepository;
        this.redisTemplate = redisTemplate;
    }

    public void iniciar(SagaCommand command) {
        log.info("{} --- Iniciando SAGA", SagaService.class);
        validarInicio(command);

        if (sagaStateRepository.existe(command.sagaId())) {
            log.warn("{} --- SAGA já existente. ID: {}", SagaService.class, command.sagaId());
            return;
        }

        SagaState sagaState = new SagaState(
            command.sagaId(),
            APROVAR_CLIENTE,
            command.payload(),
            APROVAR_SOLICITACAO,
            1
        );

        log.info("{} --- Salvando SAGA. ID: {}, Etapa: {}, Tipo: {}, Operação: {}", SagaService.class, sagaState.getSagaId(), sagaState.getEtapaAtual(), sagaState.getTipo(), sagaState.getOperacaoAtual());
        sagaStateRepository.salvar(sagaState);
        
        log.info("{} --- Salvando SAGA. ID: {}", SagaService.class, sagaState.getSagaId());
        enviar(clienteCommandProducer, sagaState, APROVAR_SOLICITACAO, command.payload());
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
            case LISTAR_GERENTES -> processarListagemGerentes(state, reply);
            case IDENTIFICAR_GERENTE -> processarIdentificacaoGerente(state, reply);
            case CRIAR_CLIENTE -> processarCriacaoCliente(state, reply);
            case CRIAR_USUARIO -> processarCriacaoUsuario(state, reply);
            case CRIAR_CONTA -> processarCriacaoConta(state, reply);
            case COMPENSAR_CRIACAO_CLIENTE -> processarCompensacaoCriacaoCliente(state, reply);
            case COMPENSAR_APROVACAO -> finalizarCompensacao(state, reply, "aprovacao");
            case MARCAR_NAO_APROVADA -> finalizarCompensacao(state, reply, "marcacao da solicitacao como nao aprovada");
            default -> log.warn("Operacao {} nao pertence a esta etapa da saga", operacao);
        }
    }

    // private void processarAprovacao(SagaState state, SagaReply reply) {
    //     if (!reply.sucesso()) {
    //         state.falhar(reply.erro());
    //         sagaStateRepository.salvar(state);
    //         return;
    //     }
    //     state.avancarPara(CRIAR_CLIENTE, 2);
    //     sagaStateRepository.salvar(state);
    //     enviar(state, CRIAR_CLIENTE);
    // }

    private void processarAprovacao(SagaState state, SagaReply reply) {
        log.info("{} --- Iniciando processarAprovacao()", SagaService.class);
        if (!reply.sucesso()) {
            log.info("{} --- SAGA com falha. Erro: {}", SagaService.class, reply.erro());
            state.falhar(reply.erro());
            sagaStateRepository.salvar(state);
            return;
        }
        
        log.info("{} --- SAGA status {}", SagaService.class, reply.status());
        ObjectNode payload = (ObjectNode) state.getPayload().deepCopy();
        payload.set("dadosSolicitacao", reply.payload());
        state.setPayload(payload);
        
        log.info("{} --- Avançar saga para LISTAR_GERENTES: {}", SagaService.class, LISTAR_GERENTES);
        state.avancarPara(LISTAR_GERENTES, 2);

        sagaStateRepository.salvar(state);
        enviar(gerenteProducer, state, LISTAR_GERENTES, objectMapper.createObjectNode());
    }

    private void processarListagemGerentes(SagaState state, SagaReply reply) {
        log.info("{} --- Iniciando processarListagemGerentes()", SagaService.class);
        if (!reply.sucesso()) {
            log.info("{} --- SAGA com falha. Erro: {}", SagaService.class, reply.erro());
            iniciarCompensacao(state, COMPENSAR_APROVACAO, reply.erro());
            return;
        }
        log.info("{} --- SAGA status {}", SagaService.class, reply.status());
        log.info("{} --- Avançar saga para IDENTIFICAR_GERENTE: {}", SagaService.class, IDENTIFICAR_GERENTE);
        state.avancarPara(IDENTIFICAR_GERENTE, 3);
        sagaStateRepository.salvar(state);
        enviar(contaProducer, state, IDENTIFICAR_GERENTE, reply.payload());
    }

    private void processarIdentificacaoGerente(SagaState state, SagaReply reply) {
        log.info("{} --- Iniciando processarIdentificacaoGerente()", SagaService.class);
        if (!reply.sucesso()) {
            log.info("{} --- SAGA com falha. Erro: {}", SagaService.class, reply.erro());
            iniciarCompensacao(state, COMPENSAR_APROVACAO, reply.erro());
            return;
        }
        log.info("{} --- SAGA status {}", SagaService.class, reply.status());
        ObjectNode payload = (ObjectNode) state.getPayload().deepCopy();
        payload.put("gerenteId", reply.payload().get("gerenteId").asLong());
        state.setPayload(payload);
        
        log.info("{} --- Avançar saga para CRIAR_CLIENTE: {}", SagaService.class, CRIAR_CLIENTE);
        state.avancarPara(CRIAR_CLIENTE, 4);
        sagaStateRepository.salvar(state);
        enviar(clienteCommandProducer, state, CRIAR_CLIENTE, state.getPayload());
    }

    private void processarCriacaoCliente(SagaState state, SagaReply reply) {
        log.info("{} --- Iniciando processarCriacaoCliente()", SagaService.class);
        if (!reply.sucesso()) {
            log.info("{} --- SAGA com falha. Erro: {}", SagaService.class, reply.erro());
            iniciarCompensacao(state, COMPENSAR_APROVACAO, reply.erro());
            return;
        }
        log.info("{} --- Avançar saga para CRIAR_USUARIO: {}", SagaService.class, CRIAR_USUARIO);
        state.avancarPara(CRIAR_USUARIO, 5);
        sagaStateRepository.salvar(state);
        
        log.info("{} --- SAGA status {}", SagaService.class, reply.status());
        String email = state.getPayload().get("dadosSolicitacao").get("email").asString();
        ObjectNode authPayload = objectMapper.createObjectNode().put("cpf", state.cpf()).put("email", email);
        enviar(authProducer, state, CRIAR_USUARIO, authPayload);
    }

    private void processarCriacaoUsuario(SagaState state, SagaReply reply) {
        log.info("{} --- Iniciando processarCriacaoUsuario()", SagaService.class);
        if (!reply.sucesso()) {
            log.info("{} --- SAGA com falha. Erro: {}", SagaService.class, reply.erro());
            iniciarCompensacao(state, COMPENSAR_CRIACAO_CLIENTE, reply.erro());
            return;
        }

        String senhaClara = reply.payload().get("senhaTemporariaTransacional").asString(); // Senha que vem do MS Auth
        ObjectNode payloadState = (ObjectNode) state.getPayload();
        payloadState.put("senhaTemporariaTransacional", senhaClara);
        state.setPayload(payloadState);
        log.info("{} --- SAGA state: {}", SagaService.class, state.getPayload());

        log.info("{} --- Avançar saga para CRIAR_CONTA: {}", SagaService.class, CRIAR_CONTA);
        state.avancarPara(CRIAR_CONTA, 6);
        sagaStateRepository.salvar(state);

        log.info("{} --- SAGA status {}", SagaService.class, reply.status());
        log.info("{} --- SAGA reply {}", SagaService.class, reply);
        log.info("{} --- SAGA state {}", SagaService.class, state.getPayload());
        ObjectNode contaPayload = objectMapper.createObjectNode()
                .put("cpf", state.cpf())
                .put("gerenteId", state.getPayload().get("gerenteId").asString())
                .put("senhaTemporariaTransacional", senhaClara);
        
        log.info("{} --- SAGA contaPayload {}", SagaService.class, contaPayload);
        enviar(contaProducer, state, CRIAR_CONTA, contaPayload);
    }

    private void processarCriacaoConta(SagaState state, SagaReply reply) {
        log.info("{} --- Iniciando processarCriacaoConta()", SagaService.class);
        if (!reply.sucesso()) {
            log.info("{} --- SAGA com falha. Erro: {}", SagaService.class, reply.erro());
            iniciarCompensacao(state, COMPENSAR_CRIACAO_CLIENTE, reply.erro());
            return;
        }
        
        log.info("{} --- SAGA status {}", SagaService.class, reply.status());
        log.info("{} --- SAGA reply {}", SagaService.class, reply);
        log.info("{} --- SAGA state {}", SagaService.class, state.getPayload());
        // ULTIMO PASSO: Enviar Email (Fire and forget)
        String email = state.getPayload().get("dadosSolicitacao").get("email").asString();
        String senhaClara = state.getPayload().get("senhaTemporariaTransacional").asString();
        
        log.info("{} --- SAGA email: {}", SagaService.class, email);
        log.info("{} --- SAGA senhaClara: {}", SagaService.class, senhaClara);

        ObjectNode emailPayload = objectMapper.createObjectNode()
            .put("email", email)
            .put("senha", senhaClara);
        
        log.info("{} --- SAGA emailPayload: {}", SagaService.class, emailPayload);
        enviar(emailProducer, state, ENVIAR_SENHA, emailPayload);
        concluirSaga(state);
    }

    

    // private void processarCriacao(SagaState state, SagaReply reply) {
    //     if (reply.sucesso()) {
    //         state.concluir();
    //         sagaStateRepository.salvar(state);
    //         log.info("Saga {} concluiu a parte do MS Cliente", state.sagaId());
    //         return;
    //     }
    //     state.compensar(COMPENSAR_APROVACAO, 3, reply.erro());
    //     sagaStateRepository.salvar(state);
    //     enviar(state, COMPENSAR_APROVACAO);
    // }

    private void iniciarCompensacao(SagaState state, String comandoCompensacao, String erro) {
        log.info("{} --- Iniciando compensação SAGA ID: [{}]", SagaService.class, state.getSagaId());
        log.info("{} --- Erro: {}", SagaService.class, erro);
        state.compensar(comandoCompensacao, state.getEtapaAtual(), erro);
        sagaStateRepository.salvar(state);
        enviar(clienteCommandProducer, state, comandoCompensacao, objectMapper.createObjectNode().put("cpf", state.cpf()));
    }

    private void processarCompensacaoCriacaoCliente(SagaState state, SagaReply reply) {
        if (!reply.sucesso()) {
            log.warn("{} --- SAGA COM ERRO. Status: {}", SagaService.class, reply.erro());
            finalizarCompensacao(state, reply, "criacao do cliente");
            return;
        }

        if ("EMAIL_DUPLICADO".equals(state.erro())) {
            log.warn("{} --- SAGA COM ERRO. EMAIL_DUPLICADO: {}", SagaService.class, reply.erro());
            ObjectNode motivo = objectMapper.createObjectNode()
                    .put("cpf", state.cpf())
                    .put("motivo", "E-mail já cadastrado");
            state.compensar(MARCAR_NAO_APROVADA, state.getEtapaAtual(), state.erro());
            sagaStateRepository.salvar(state);
            enviar(clienteCommandProducer, state, MARCAR_NAO_APROVADA, motivo);
            return;
        }

        iniciarCompensacao(state, COMPENSAR_APROVACAO, state.erro());
    }

    private void finalizarCompensacao(SagaState state, SagaReply reply, String etapaCompensada) {
        if (!reply.sucesso()) {
            log.warn("{} --- SAGA COM ERRO. Status: {}", SagaService.class, reply.erro());
            String erroOriginal = state.erro();
            falharSaga(
                    state,
                    "Falha ao compensar " + etapaCompensada + ": " + reply.erro()
                            + ". Erro original: " + erroOriginal
            );
            log.info("{} --- Erro: {}", SagaService.class, erroOriginal);
            return;
        }

        String erroOriginal = state.erro();
        log.info("Saga {} compensada com sucesso ate a etapa de {}", state.sagaId(), etapaCompensada);
        falharSaga(state, erroOriginal);
    }

    private void concluirSaga(SagaState state) {
        log.info("{} --- Iniciando concluirSaga()", SagaService.class);
        state.concluir();
        sagaStateRepository.salvar(state);
        log.info("{} --- Finalizando SAGA... : ID: [{}]", SagaService.class, state.sagaId());
        try {
            //Map<String, Object> job = Map.of("jobId", state.sagaId(), "status", "CONCLUIDO", "resultType", "resource", "dominio", "clientes", "resourceId", state.cpf(), "erro", null);
            ObjectNode job = objectMapper.createObjectNode()
                .put("jobId", state.sagaId())
                .put("status", "CONCLUIDO")
                .put("resultType", "resource")
                .put("dominio", "clientes")
                .put("resourceId", state.cpf())
                .putNull("erro");

            log.info("{} --- Finalizando SAGA... : job: [{}]", SagaService.class, job);

            redisTemplate.opsForValue().set("job:" + state.sagaId(), objectMapper.writeValueAsString(job));
            redisTemplate.delete("cache:cliente:" + state.cpf()); // Invalida cache do Gateway
            log.info("{} --- SAGA [{}] FINALIZADA COM SUCESSO!", SagaService.class, state.sagaId());
        } catch (Exception e) { log.error("Erro no Job Redis", e); }
    }

    private void falharSaga(SagaState state, String erro) {
        state.falhar(erro);
        sagaStateRepository.salvar(state);
        log.error("Saga {} FALHOU: {}", state.sagaId(), erro);
        try {
            Map<String, Object> job = Map.of("jobId", state.sagaId(), "status", "FALHA", "resultType", null, "dominio", "clientes", "resourceId", null, "erro", erro != null ? erro : "Erro interno");
            redisTemplate.opsForValue().set("job:" + state.sagaId(), objectMapper.writeValueAsString(job));
        } catch (Exception e) { log.error("Erro no Job Redis"); }
    }

    private void enviar(BaseCommandProducer producer, SagaState state, String tipo, JsonNode payload) {
        log.info("{} --- Enviando SAGA tipo {} para {}", SagaService.class, tipo, producer.getClass().getSimpleName());
        log.info("{} --- Enviando SAGA payload: {}", SagaService.class, payload);
        producer.enviar(state.sagaId(), tipo, payload); 
    }

    // Alterando para utilizar herança a fim de evitar repetição de código
    
    // private void enviar(Object producer, SagaState state, String tipo, JsonNode payload) {
    //     log.info("{} --- Enviando SAGA {}", SagaService.class, payload);
    //     if (producer instanceof ClienteCommandProducer c) c.enviar(state.sagaId(), tipo, payload);
    //     else if (producer instanceof GerenteCommandProducer g) g.enviar(state.sagaId(), tipo, payload);
    //     else if (producer instanceof ContaCommandProducer ct) ct.enviar(state.sagaId(), tipo, payload);
    //     else if (producer instanceof AuthCommandProducer a) a.enviar(state.sagaId(), tipo, payload);
    //     else if (producer instanceof EmailCommandProducer e) e.enviar(state.sagaId(), tipo, payload);
    // }

    // private void enviar(SagaState state, String tipo) {
    //     JsonNode payload = objectMapper.createObjectNode().put("cpf", state.cpf());
    //     clienteCommandProducer.enviar(state.sagaId(), tipo, payload);
    // }

    private void validarInicio(SagaCommand command) {
        if (command == null || !APROVAR_CLIENTE.equals(command.tipo()))
            throw new IllegalArgumentException("tipo de saga nao suportado");
        if (command.payload() == null || command.payload().get("cpf") == null
                || command.payload().get("cpf").asString().isBlank())
            throw new IllegalArgumentException("cpf e obrigatorio para aprovar cliente");
    }
}
