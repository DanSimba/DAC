package com.monsterbank.ms_auth.exception;

public class UsuarioInativoException extends RuntimeException {
    public UsuarioInativoException() {
        super("Usuário inativo");
    }

}
