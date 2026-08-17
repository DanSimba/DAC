package com.monsterbank.ms_cliente.exception;

public class EmailSolicitadoException extends RuntimeException{

    public EmailSolicitadoException(){
        super("Email já utilizado por um usuário!");
    }
}
