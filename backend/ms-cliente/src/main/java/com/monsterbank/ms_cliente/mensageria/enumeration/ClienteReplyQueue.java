package com.monsterbank.ms_cliente.mensageria.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum ClienteReplyQueue {

    APROVAR_SOLICITACAO(
            "cliente.aprovar-solicitacao",
            "ms.cliente.aprovar-solicitacao.reply"
    ),
    CRIAR(
            "cliente.criar",
            "ms.cliente.criar.reply"
    ),
    COMPENSAR_APROVACAO(
            "cliente.compensar-aprovacao",
            "ms.cliente.compensar-aprovacao.reply"
    ),
    MARCAR_NAO_APROVADA(
            "cliente.marcar-nao-aprovada",
            "ms.cliente.marcar-nao-aprovada.reply"
    ),
    COMPENSAR_CRIACAO(
            "cliente.compensar-criacao",
            "ms.cliente.compensar-criacao.reply"
    ),
    COMANDO_DESCONHECIDO(
            null,
            "ms.cliente.comando-desconhecido.reply"
    );

    private final String commandType;
    private final String queueName;

    ClienteReplyQueue(String commandType, String queueName) {
        this.commandType = commandType;
        this.queueName = queueName;
    }

    public String queueName() {
        return queueName;
    }

    public static Optional<ClienteReplyQueue> fromCommandType(String commandType) {
        return Arrays.stream(values())
                .filter(queue -> queue.commandType != null)
                .filter(queue -> queue.commandType.equals(commandType))
                .findFirst();
    }
}
