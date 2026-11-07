package com.monsterbank.ms_account.account.accountDTOs;
import java.math.BigDecimal;

//MONTA A ACOCUNT ATUALIZADA COM BASE NOS EVENTOS E MANDA PRO FRONT
public class AccountLastVersion {
    BigDecimal balanco;
    Long managerId; //CPF GERENTE
    String datetime; //DATA DA ULTIMA ATUALIZAÇÃO 

    public AccountLastVersion(){};

    public AccountLastVersion(BigDecimal balanco, Long managerId, String datetime){
        this.balanco = balanco;
        this.managerId = managerId;
        this.datetime = datetime;
    }
}
