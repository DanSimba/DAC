package com.monsterbank.ms_account.exceptions;

public class ErroContaNaoEncontradaException extends RuntimeException{
     public ErroContaNaoEncontradaException() {
        super("Conta de destino não encontrada!!! :(");
    }
}
