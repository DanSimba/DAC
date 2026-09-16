package com.monsterbank.ms_account.account.accountDTOs;

public class AccountDTO {
    String number;
    String client_cpf;
    double balanco;
    Long manager_id;
    
    public AccountDTO(
        String number,
        String clientCpf,
        double balanco,
        Long managerId
    ){
        this.number = number;
        this.client_cpf = clientCpf;
        this.balanco = balanco;
        this.manager_id = managerId;
    }

}
