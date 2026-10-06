package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SagaService {

    private static final Logger log = LoggerFactory.getLogger(SagaService.class);

    /**
     * Ponto unico de entrada das respostas. A continuacao/compensacao da saga pode ser
     * acrescentada aqui sem acoplar os listeners aos demais microsservicos.
     */
    public void processarResposta(String operacao, SagaReply reply) {
        if (reply.sucesso()) {
            log.info("Saga {}: operacao {} concluida com sucesso", reply.sagaId(), operacao);
            return;
        }

        log.warn(
                "Saga {}: operacao {} falhou. Erro: {}",
                reply.sagaId(),
                operacao,
                reply.erro()
        );
    }
}
