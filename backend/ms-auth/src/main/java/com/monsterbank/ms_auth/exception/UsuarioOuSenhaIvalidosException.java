package com.monsterbank.ms_auth.exception;

public class UsuarioOuSenhaIvalidosException extends RuntimeException {
    public UsuarioOuSenhaIvalidosException() {
        super("Usuário ou senha inválidos");
    }
}
