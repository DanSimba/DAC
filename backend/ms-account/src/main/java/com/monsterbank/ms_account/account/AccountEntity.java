package com.monsterbank.ms_account.account;

import jakarta.persistence.*;

import java.util.concurrent.ThreadLocalRandom;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "account")
public class AccountEntity {

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
    private Integer createdAt; //PADRAO AAAAMMDD
    
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

        //NESTE CASO, DIFERENTE DO EXTRATO, EU POSSO CRIAR A DATA AQ PQ 
        // CREATED_AT NÃO É UM ID QUE EU USO PRA BUSCAR A CONTA
        LocalDate now = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        this.createdAt = Integer.parseInt( //NOWID É COM BASE NO HORÁRIO DE SP***
            now.format(DateTimeFormatter.BASIC_ISO_DATE)
        );

    }

    public AccountEntity(){} //CONSTRUTOR QUE O HIBERNATE USA PARA DEVOLVER OQ EU BUSCO DO REPO

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
}
