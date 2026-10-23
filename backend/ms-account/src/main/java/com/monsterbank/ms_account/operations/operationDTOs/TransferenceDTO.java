package com.monsterbank.ms_account.operations.operationDTOs;

import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.TransferenceEntity;

public class TransferenceDTO {

    private String type = "transference";
    private String cpfOrigin;
    private String nameOrigin;
    private String accOrigin;
    private String accDestiny;
    private BigDecimal value;
    private String datetime;

    //CONSTRUTOR DO JACKSON
    public TransferenceDTO(){};
    
    //CONSTRUTOR QUE O EXTRATODTO USA PRA CRIA AS LISTAS COM BASE NO CONTEUDO DE TRANSFS DO EXTRATOENTITY
    public TransferenceDTO(TransferenceEntity te){
        this.cpfOrigin = te.getCpfOrigin();
        this.nameOrigin = te.getNameOrigin();
        this.accOrigin = te.getAccOrigin();
        this.accDestiny = te.getAccDestiny();
        this.value = te.getValue();
        this.datetime = te.getDatetime();
    }

    public TransferenceDTO(String cpfOrigin, String nameOrigin, String accOrigin, String accDestiny, BigDecimal value, String datetime){
        this.cpfOrigin = cpfOrigin;
        this.nameOrigin = nameOrigin;
        this.accOrigin = accOrigin;
        this.accDestiny = accDestiny;
        this.value = value;
        this.datetime = datetime;
    }

    //GETTERS E SETTERS SÓ PRO JACKSON NÃO DAR PITI
    public String getType() {
        return type;
    }

    public String getCpfOrigin() {
        return cpfOrigin;
    }

    public String getNameOrigin() {
        return nameOrigin;
    }

    public String getAccOrigin() {
        return accOrigin;
    }

    public String getAccDestiny() {
        return accDestiny;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCpfOrigin(String cpfOrigin) {
        this.cpfOrigin = cpfOrigin;
    }

    public void setNameOrigin(String nameOrigin) {
        this.nameOrigin = nameOrigin;
    }

    public void setAccOrigin(String accOrigin) {
        this.accOrigin = accOrigin;
    }

    public void setAccDestiny(String accDestiny) {
        this.accDestiny = accDestiny;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    } 
}
