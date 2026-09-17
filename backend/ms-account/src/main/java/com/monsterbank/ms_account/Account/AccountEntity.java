package com.monsterbank.ms_account.account;

import jakarta.persistence.*;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String number;

    @Column(length = 11, nullable = false)
    private String client_cpf;

    @Column(nullable = false, precision = 19, scale = 4)
    private double balanco;

    @Column(nullable = false)
    private Long manager_id;
    
    public AccountEntity(
        String clientCpf,
        double balanco,
        Long managerId
    ){
        //pode gerar um valor repetido, mas a chance é mínima
        this.number = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        this.client_cpf = clientCpf;
        this.balanco = balanco;
        this.manager_id = managerId;
    }

    public String getNumber() {
        return this.number;
    }

    public String getClientCpf() {
        return this.client_cpf;
    }

    public double getBalanco() {
        return this.balanco;
    }

    public Long getManagerId() {
        return this.manager_id;
    }
}
