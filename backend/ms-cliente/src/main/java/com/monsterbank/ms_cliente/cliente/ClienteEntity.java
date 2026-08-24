package com.monsterbank.ms_cliente.cliente;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "clientes", uniqueConstraints = {@UniqueConstraint(name = "uk_solicitacao_email", columnNames = "email")})
public class ClienteEntity {


    @Id
    @Column(length = 11, nullable = false)
    String cpf;

    @Column(nullable = false)
    String nome;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal salario;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private Integer numero;

    private String complemento;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;


    public ClienteEntity(
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
            String uf
    ){
     this.cpf=cpf;
     this.nome=nome;
     this.email=email;
     this.telefone=telefone;
     this.salario=salario;
     this.logradouro=logradouro;
     this.numero = numero;
     this.complemento=complemento;
     this.cep=cep;
     this.cidade=cidade;
     this.uf=uf;

    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getUf() {
        return uf;
    }
}
