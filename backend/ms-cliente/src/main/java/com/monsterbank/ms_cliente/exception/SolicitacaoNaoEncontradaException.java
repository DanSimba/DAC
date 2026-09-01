package com.monsterbank.ms_cliente.exception;

public class SolicitacaoNaoEncontradaException extends RuntimeException {
    public SolicitacaoNaoEncontradaException() {
        super("Solicitacão com esse cpf não encontrada!");
    }
}
