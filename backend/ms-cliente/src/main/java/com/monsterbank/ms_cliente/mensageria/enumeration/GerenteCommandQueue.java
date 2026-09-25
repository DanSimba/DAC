package com.monsterbank.ms_cliente.mensageria.enumeration;

public enum GerenteCommandQueue {

    AVALIAR_SOLICITACAO(
        "gerente.avaliar-solicitacao",
        "ms.gerente.cmd"
    );

    private final String commandType;
    private final String queueName;

    GerenteCommandQueue(String commandType, String queueName) {
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
