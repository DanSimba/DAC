package com.monsterbank.ms_cliente.exception;

public class ErroCriacaoClienteException extends RuntimeException {
    public ErroCriacaoClienteException() {
        super("Erro na criação de cliente!");
    }
}
