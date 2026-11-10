package com.monsterbank.ms_cliente.cliente.clienteDTOs;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class ClienteRetorno extends RepresentationModel<ClienteRetorno> {

    private final String cpf;
    private final String nome;
    private final String email;
    private final String telefone;
    private final BigDecimal salario;
    private final String logradouro;
    private final Integer numero;
    private final String complemento;
    private final String cep;
    private final String cidade;
    private final String estado;

    public ClienteRetorno(
            String cpf,
            String nome,
            String email,
            String telefone,
            BigDecimal salario,
            String logradouro,
            Integer numero,
            String complemento,
            String cep,
            String cidade,
            String estado
    ) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.salario = salario;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public BigDecimal getSalario() { return salario; }
    public String getLogradouro() { return logradouro; }
    public Integer getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getCep() { return cep; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
}
