package com.monsterbank.ms_account.exceptions;

public class SaldoInsuficienteException extends RuntimeException{
    public SaldoInsuficienteException(){
         super("Saldo insuficiente para esta transação!!! (pobe xD)");
    }
}
