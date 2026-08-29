package com.monsterbank.ms_cliente.exception;

public class SolicitacaoNaoEncontradaException extends RuntimeException{
    public SolicitacaoNaoEncontradaException(){
        super("Solicitação não encontrada!");
    }
}
