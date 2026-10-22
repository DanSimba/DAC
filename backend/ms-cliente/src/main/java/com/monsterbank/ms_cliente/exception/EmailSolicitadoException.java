package com.monsterbank.ms_cliente.exception;

public class EmailSolicitadoException extends RuntimeException{

    public EmailSolicitadoException(){
        super("CPF já possui solicitação/conta, ou e-mail já usado em outra solicitação");
    }
}
