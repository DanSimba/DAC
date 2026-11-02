package com.monsterbank.ms_account.account.accountDTOs;
import java.math.BigDecimal;

public class AccountDTO {
    String number;
    String clientCpf;
    BigDecimal balanco;
    Long managerId;
    private Integer createdAt;
    
    public AccountDTO(
        String number,
        String clientCpf,
        BigDecimal balanco,
        Long managerId,
        Integer createdAt //aqui eu passo pq a conta já existe, não crio outra data
    ){
        this.number = number;
        this.clientCpf = clientCpf;
        this.balanco = balanco;
        this.managerId = managerId;
        this.createdAt = createdAt;
    }

    public String getNumber() {
        return number;
    }

    public String getClientCpf() {
        return clientCpf;
    }

    public BigDecimal getBalanco() {
        return balanco;
    }

    public Long getManagerId() {
        return managerId;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

}
