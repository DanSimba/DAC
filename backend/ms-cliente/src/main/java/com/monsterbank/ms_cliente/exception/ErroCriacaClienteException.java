package com.monsterbank.ms_cliente.exception;

public class ErroCriacaClienteException extends RuntimeException {
    public ErroCriacaClienteException() {
        super("Erro na criação de cliente!");
    }
}
