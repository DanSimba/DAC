package com.monsterbank.ms_cliente.cliente.clienteDTOs;

public record ClienteRetorno(
        String cpf,
        String nome,
        String cidade,
        String estado,
        String Saldo
) {
}
