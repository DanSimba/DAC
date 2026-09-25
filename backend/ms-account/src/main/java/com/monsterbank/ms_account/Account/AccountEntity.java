package com.monsterbank.ms_account.account;

import jakarta.persistence.*;

import java.util.concurrent.ThreadLocalRandom;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String number;

    @Column(name = "client_cpf", length = 11, nullable = false)
    private String clientCpf;

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal balanco;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;
    
    public AccountEntity(
        String clientCpf,
        BigDecimal balanco,
        Long managerId
    ){
        //pode gerar um valor repetido, mas a chance é mínima
        this.number = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        this.clientCpf = clientCpf;
        this.balanco = balanco;
        this.managerId = managerId;
    }

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

    //SETTERS---------------0987667890-=-098UYGKLOIU

    public void setBalanco(BigDecimal balanco) {
        this.balanco = balanco;
    }
}
