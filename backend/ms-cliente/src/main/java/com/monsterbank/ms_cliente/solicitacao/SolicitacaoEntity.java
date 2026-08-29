package com.monsterbank.ms_cliente.solicitacao;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_cliente", uniqueConstraints = {@UniqueConstraint(name = "uk_solicitacao_email", columnNames = "email")})
public class SolicitacaoEntity {

    @Id
    @Column(length = 11, nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

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
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    private String motivo;

    private LocalDateTime dataProcessamento;

    private LocalDateTime dataSolicitacao;

    // Mantido protected por necessidade do JPA, caso necessário, alterar para public
    protected SolicitacaoEntity(){};

    public SolicitacaoEntity(
            String cpf,
            String nome,
            String email,
            String telefone,
            BigDecimal salario,
            String logradouro,
            Integer numero,
            String complemento,
            String cep,
            String bairro,
            String cidade,
            String uf)  {

        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.salario = salario;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.cep = cep;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataProcessamento = null;
        this.dataSolicitacao = LocalDateTime.now();
    }



    public void aprovar(){
        this.status = StatusSolicitacao.APROVADO;
        this.motivo = null;
        this.dataProcessamento = LocalDateTime.now();
    }

    public void rejeitar(String motivo){
        this.status = StatusSolicitacao.NAO_APROVADO;
        this.motivo = motivo;
        this.dataProcessamento = LocalDateTime.now();
    }


    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getCep() {
        return cep;
    }


    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }





}
