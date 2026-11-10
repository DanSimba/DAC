package com.monsterbank.ms_account.account.accountDTOs;

public class CreateAccountRequest {
    private String cpf;
    private Long managerId;

    public CreateAccountRequest(String c, Long mi){
        this.cpf = c;
        this.managerId = mi;
    }

    public String getCpf() {
        return cpf;
    }

    public Long getManagerId() {
        return managerId;
    }
}
