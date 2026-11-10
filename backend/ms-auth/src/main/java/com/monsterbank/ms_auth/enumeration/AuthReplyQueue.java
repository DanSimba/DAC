package com.monsterbank.ms_auth.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum AuthReplyQueue {

    CRIAR_USUARIO(
        "auth.criar-usuario",
        "ms.auth.criar-usuario.reply"
    ),
    COMANDO_DESCONHECIDO(
            null,
            "ms.cliente.comando-desconhecido.reply"
    );

    private final String commandType;
    private final String queueName;

    AuthReplyQueue(String commandType, String queueName) {
        this.commandType = commandType;
        this.queueName = queueName;
    }

    public String queueName() {
        return queueName;
    }

    public static Optional<AuthReplyQueue> fromCommandType(String commandType) {
        return Arrays.stream(values())
                .filter(queue -> queue.commandType != null)
                .filter(queue -> queue.commandType.equals(commandType))
                .findFirst();
    }
}
