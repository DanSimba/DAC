package com.monsterbank.ms_email.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum EmailReplyQueue {
    REJEITAR_SOLICITACAO(
        "email.rejeitar-solicitacao",
        "ms.email.rejeitar-solicitacao.reply"
    ),
    APROVAR_SOLICITACAO(
        "email.aprovar-solicitacao",
        "ms.email.aprovar-solicitacao.reply"
    ),
    COMANDO_DESCONHECIDO(
            null,
            "ms.cliente.comando-desconhecido.reply"
    );

    private final String commandType;
    private final String queueName;

    EmailReplyQueue(String commandType, String queueName) {
        this.commandType = commandType;
        this.queueName = queueName;
    }

    public String commandType() {
        return commandType;
    }

    public String queueName() {
        return queueName;
    }

    public static Optional<EmailReplyQueue> fromCommandType(String commandType) {
        return Arrays.stream(values())
                .filter(queue -> queue.commandType != null)
                .filter(queue -> queue.commandType.equals(commandType))
                .findFirst();
    }
}
