package com.monsterbank.ms_account.exceptions;

public class NullTransactionException extends RuntimeException{
    public NullTransactionException() {
        super("Valor da transação é 0!!! >:(");
    }
}
