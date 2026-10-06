package com.monsterbank.ms_orquestrador.saga;

import com.monsterbank.ms_orquestrador.messaging.dto.SagaCommand;
import com.monsterbank.ms_orquestrador.messaging.dto.SagaReply;
import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SagaService {

    public static final String APROVAR_CLIENTE = "aprovar-cliente";
    public static final String APROVAR_SOLICITACAO = "cliente.aprovar-solicitacao";

    private static final Logger log = LoggerFactory.getLogger(SagaService.class);
    private final ClienteCommandProducer clienteCommandProducer;

    public SagaService(ClienteCommandProducer clienteCommandProducer) {
        this.clienteCommandProducer = clienteCommandProducer;
    }

    public void iniciar(SagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("comando da saga e obrigatorio");
        }

        if (!APROVAR_CLIENTE.equals(command.tipo())) {
            throw new IllegalArgumentException("tipo de saga nao suportado: " + command.tipo());
        }

        if (command.payload() == null
                || command.payload().get("cpf") == null
                || command.payload().get("cpf").asString().isBlank()) {
            throw new IllegalArgumentException("cpf e obrigatorio para aprovar cliente");
        }

        log.info("Saga {}: iniciando aprovacao do cliente", command.sagaId());
        clienteCommandProducer.enviar(
                command.sagaId(),
                APROVAR_SOLICITACAO,
                command.payload()
        );
    }

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
