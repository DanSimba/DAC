package com.monsterbank.ms_account.exceptions;

public class ErroListaDeGerenteVaziaOuNula extends RuntimeException {
    public ErroListaDeGerenteVaziaOuNula() {
            super("Lista de gerentes vazia ou nula.");
        }
}
