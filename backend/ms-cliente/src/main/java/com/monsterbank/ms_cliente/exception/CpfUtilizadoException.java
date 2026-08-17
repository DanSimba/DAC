package com.monsterbank.ms_cliente.exception;

public class CpfUtilizadoException extends RuntimeException{

    public CpfUtilizadoException(){
        super("CPF já utilizado por outro cliente!");
    }
}
