package com.monsterbank.ms_cliente.exception;

public class SalarioInvalidoException extends RuntimeException {
    public SalarioInvalidoException(){
        super("Falha ao converter Salário, valor incompatível!");
    }
}
