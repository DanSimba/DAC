package com.monsterbank.ms_account.Account.AccountDTOs;
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

    public BigDecimal getBalanco() {
        return balanco;
    }

    public String getDatetime() {
        return datetime;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setBalanco(BigDecimal balanco) {
        this.balanco = balanco;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
}
