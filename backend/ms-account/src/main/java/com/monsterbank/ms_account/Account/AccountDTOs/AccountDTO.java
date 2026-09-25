package com.monsterbank.ms_account.account.accountDTOs;
import java.math.BigDecimal;

public class AccountDTO {
    String number;
    String clientCpf;
    BigDecimal balanco;
    Long managerId;
    
    public AccountDTO(
        String number,
        String clientCpf,
        BigDecimal balanco,
        Long managerId
    ){
        this.number = number;
        this.clientCpf = clientCpf;
        this.balanco = balanco;
        this.managerId = managerId;
    }

}
