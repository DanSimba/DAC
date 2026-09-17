package com.monsterbank.ms_account.exceptions;

public class ErroCriacaoAccountException extends RuntimeException{
    public ErroCriacaoAccountException() {
        super("Erro na criação da conta!");
    }
}
