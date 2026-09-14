package com.monsterbank.ms_account.account;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class AccountEntity {
    @Id 
    @Column(nullable = false)
    String number;

    @Column(length = 11, nullable = false)
    String client_cpf;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanco;

    @Column(nullable = false)
    Integer manager_id;

    @Column(length = 18, nullable = false) //string data tem 16 chars, mas vou colocar 18 só pra garantir
    String created_at;  
    
    public accountEntity(
        String number,
        String clientCpf,
        BigDecimal balanco,
        Integer managerId,
        String createdAt
    ){
        this.number = number;
        this.client_cpf = clientCpf;
        this.balanco = balanco;
        this.manager_id = managerId;
        this.created_at = createdAt;
    }

    public String getNumber() {
        return number;
    }

    public String getClient_cpf() {
        return client_id;
    }

    public BigDecimal getBalanco() {
        return balanco;
    }

    public Integer getManager_id() {
        return manager_id;
    }

    public String getCreated_at() {
        return created_at;
    }
}
