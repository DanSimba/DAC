package com.monsterbank.ms_account.mensageria.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum AccountReplyQueue {

    APROVAR_SOLICITACAO(
            "conta.aprovar-solicitacao",
            "ms.conta.aprovar-solicitacao.reply"
    ),
    IDENTIFICAR_GERENTE(
        "conta.identificar-gerente",
        "ms.conta.identificar-gerente.reply"
    ),
    CRIAR(
            "conta.criar",
            "ms.conta.criar.reply"
    ),
    COMPENSAR_APROVACAO(
            "conta.compensar-aprovacao",
            "ms.conta.compensar-aprovacao.reply"
    ),
    MARCAR_NAO_APROVADA(
            "conta.marcar-nao-aprovada",
            "ms.conta.marcar-nao-aprovada.reply"
    ),
    COMPENSAR_CRIACAO(
            "conta.compensar-criacao",
            "ms.conta.compensar-criacao.reply"
    ),
    COMANDO_DESCONHECIDO(
            null,
            "ms.conta.comando-desconhecido.reply"
    );

    private final String commandType;
    private final String queueName;

    AccountReplyQueue(String commandType, String queueName) {
        this.commandType = commandType;
        this.queueName = queueName;
    }

    public String queueName() {
        return queueName;
    }

    public static Optional<AccountReplyQueue> fromCommandType(String commandType) {
        return Arrays.stream(values())
                .filter(queue -> queue.commandType != null)
                .filter(queue -> queue.commandType.equals(commandType))
                .findFirst();
    }
}
