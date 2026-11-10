package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrarSolicitacaoRequest(
        @NotBlank @Size(max = 150)
        String nome,
        @NotBlank @Email @Size(max = 254)
        String email,
        @NotBlank @Pattern(regexp = "\\d{11}")
        String cpf,
        @NotBlank @Size(max = 30)
        String telefone,
        @NotBlank @Pattern(regexp = "\\d+(\\.\\d{1,4})?")
        String salario,
        @NotNull @Valid
        Endereco endereco
) {}
