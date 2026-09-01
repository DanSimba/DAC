package com.monsterbank.ms_cliente.exception;

public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException() {
        super("CPF Inválido");
    }
}
