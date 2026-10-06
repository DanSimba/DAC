package com.monsterbank.ms_cliente.mensageria.enumeration;

public enum EmailCommandQueue {
    
    REJEITAR_SOLICITACAO(
        "email.rejeitar-solicitacao",
        "ms.email.cmd"
    );

    private final String commandType;
    private final String queueName;

    EmailCommandQueue(String commandType, String queueName) {
        this.commandType = commandType;
        this.queueName = queueName;
    }

    public String commandType() {
        return commandType;
    }

    public String queueName() {
        return queueName;
    }
}
