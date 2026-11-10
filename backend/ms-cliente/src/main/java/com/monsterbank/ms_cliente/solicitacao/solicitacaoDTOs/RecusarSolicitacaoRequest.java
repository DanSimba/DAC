package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecusarSolicitacaoRequest(
    @NotBlank @Size(max = 1000)
    String motivo
) {
}
