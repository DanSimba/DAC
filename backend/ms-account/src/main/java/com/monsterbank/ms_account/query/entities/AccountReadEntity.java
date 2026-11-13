package com.monsterbank.ms_account.query.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class AccountReadEntity {

    @Id
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "client_cpf", length = 11, nullable = false, unique = true)
    private String clientCpf;

    @Column(name = "balanco", nullable = false, precision = 17, scale = 2)
    private BigDecimal balanco;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Column(name = "created_at", nullable = false)
    private Integer createdAt;
    
    //SEM CONSTRUTOR DE CONTA NOVA, AQUI SÓ DEVE TER CONTAS QUE JÁ EXISTEM E FORAM COLOCAAS NO BANCO DE ESCRITA

    public AccountReadEntity(){} //CONSTRUTOR QUE O HIBERNATE USA PARA DEVOLVER OQ EU BUSCO DO REPO

    public String getNumber() {
        return this.number;
    }

    public String getClientCpf() {
        return this.clientCpf;
    }

    public BigDecimal getBalanco() {
        return this.balanco;
    }

    public Long getManagerId() {
        return this.managerId;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    //SETTERS---------------0987667890-=-098UYGKLOIU

     public void setBalanco(BigDecimal balanco) {
        this.balanco = balanco;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public void setClientCpf(String clientCpf) {
        this.clientCpf = clientCpf;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
