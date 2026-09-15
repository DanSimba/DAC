package com.monsterbank.ms_account.account.accountDTOs;

public class AccountDTO {
    String number;
    String client_cpf;
    BigDecimal balanco;
    Long manager_id;
    LocalDateTime created_at;  
    
    public AccountDTO(
        String number,
        String clientCpf,
        BigDecimal balanco,
        Long managerId
    ){
        this.number = number;
        this.client_cpf = clientCpf;
        this.balanco = balanco;
        this.manager_id = managerId;
    }

}
