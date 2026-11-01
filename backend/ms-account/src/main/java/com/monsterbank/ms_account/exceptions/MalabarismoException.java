package com.monsterbank.ms_account.exceptions;

public class MalabarismoException extends RuntimeException{
    public MalabarismoException() {
        super("Transferência para si mesmo!!! >:(");
    }
}
