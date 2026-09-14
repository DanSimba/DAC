package com.monsterbank.ms_cliente.exception;

public class SolicitacaoNaoPendenteException extends RuntimeException{
    public SolicitacaoNaoPendenteException (){
        super("A solicitação não está pendente!");
    }
}
