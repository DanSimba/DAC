package com.monsterbank.ms_cliente.utils;

public final class CpfUtils {

    private CpfUtils(){

    }

    public static boolean validar(String cpf) {
        if (cpf == null) {
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        return digito1 == Character.getNumericValue(cpf.charAt(9))
                && digito2 == Character.getNumericValue(cpf.charAt(10));
    }

}
