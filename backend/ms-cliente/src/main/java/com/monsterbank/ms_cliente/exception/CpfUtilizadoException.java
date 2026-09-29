package com.monsterbank.ms_cliente.exception;

public class CpfUtilizadoException extends RuntimeException{

    public CpfUtilizadoException(){
        super("CPF já possui solicitação/conta, ou e-mail já usado em outra solicitação");
    }
}
