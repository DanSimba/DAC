package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.redis.SagaStateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SagaTimeoutMonitor {
    private final SagaStateRepository stateRepository;
    private final SagaService sagaService;

    public SagaTimeoutMonitor(SagaStateRepository stateRepository, SagaService sagaService) {
        this.stateRepository = stateRepository;
        this.sagaService = sagaService;
    }

    @Scheduled(fixedDelay = 5000)
    public void verificar() {
        stateRepository.listar().stream()
                .filter(state -> !state.finalizada())
                .forEach(state -> sagaService.processarTimeout(state.sagaId()));
    }
}
