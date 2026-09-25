package com.monsterbank.ms_gerente.mensageria.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum GerenteReplyQueue {

    AVALIAR_SOLICITACAO(
        "gerente.avaliar-solicitacao",
        "ms.gerente.avaliar-solicitacao.reply"
    ),
    COMANDO_DESCONHECIDO(
            null,
            "ms.cliente.comando-desconhecido.reply"
    );

    private final String commandType;
    private final String queueName;

    GerenteReplyQueue(String commandType, String queueName) {
        this.commandType = commandType;
        this.queueName = queueName;
    }

    public String commandType() {
        return commandType;
    }

    public String queueName() {
        return queueName;
    }

    public static Optional<GerenteReplyQueue> fromCommandType(String commandType) {
        return Arrays.stream(values())
                .filter(queue -> queue.commandType != null)
                .filter(queue -> queue.commandType.equals(commandType))
                .findFirst();
    }
}
