package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record Endereco(
    @NotBlank @Pattern(regexp = "\\d{8}")
    String cep,
    @NotBlank @Size(max = 150)
    String logradouro,
    @NotNull @Positive
    Integer numero,
    @Size(max = 100)
    String complemento,
    @NotBlank @Size(max = 100)
    String bairro,
    @NotBlank @Size(max = 100)
    String cidade,
    @NotBlank @Pattern(regexp = "[A-Za-z]{2}")
    String uf
) {}
