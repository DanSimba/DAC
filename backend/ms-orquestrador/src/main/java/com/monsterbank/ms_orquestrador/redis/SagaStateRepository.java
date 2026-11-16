package com.monsterbank.ms_orquestrador.redis;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.monsterbank.ms_orquestrador.saga.SagaState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository 
public class SagaStateRepository {
    private static final Logger log = LoggerFactory.getLogger(SagaStateRepository.class);
    private static final String SAGA_PREFIX = "saga:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public SagaStateRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void salvar(SagaState sagaState) {
        log.info("{} --- Iniciando salvar SAGA para a SAGA {}", SagaStateRepository.class, sagaState.sagaId());
        try {
            String jsonSagaState = objectMapper.writeValueAsString(sagaState);
            redisTemplate.opsForValue().set(
                SAGA_PREFIX + sagaState.sagaId(),
                jsonSagaState,
                Duration.ofHours(1)
            );
        } catch (JacksonException e) {
            log.error("{} --- Erro ao serializar o estado da SAGA para o Redis", SagaStateRepository.class, e);
            throw new RuntimeException("Falha na serialização do SagaState", e);
        }
    }

    public SagaState buscarPorId(String sagaId) {
        log.info("{} --- Iniciando busca por ID SAGA {}", SagaStateRepository.class, sagaId);
        String json = redisTemplate.opsForValue().get(SAGA_PREFIX + sagaId);

        if(json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, SagaState.class);
        } catch (JacksonException e) {
            log.error("Erro ao desserializar o estado da SAGA do Redis", e);
            throw new RuntimeException("Falha na desserialização do SagaState", e);
        }
    }

    public boolean existe(String sagaId) {
        log.info("{} --- Iniciando verificação de existência da SAGA {}", SagaStateRepository.class, sagaId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(SAGA_PREFIX + sagaId));
    }

    public List<SagaState> listar() {
        Set<String> keys = redisTemplate.keys(SAGA_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return redisTemplate.opsForValue().multiGet(keys).stream()
                .filter(json -> json != null)
                .map(this::desserializar)
                .toList();
    }

    private SagaState desserializar(String json) {
        try {
            return objectMapper.readValue(json, SagaState.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Falha na desserializacao do SagaState", e);
        }
    }
}
