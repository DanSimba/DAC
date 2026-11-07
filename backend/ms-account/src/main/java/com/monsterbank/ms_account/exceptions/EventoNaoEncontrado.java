package com.monsterbank.ms_account.exceptions;

public class EventoNaoEncontrado extends RuntimeException{
    public EventoNaoEncontrado() {
        super("Erro ao procurar evento(s)!!! :(");
    }
}
