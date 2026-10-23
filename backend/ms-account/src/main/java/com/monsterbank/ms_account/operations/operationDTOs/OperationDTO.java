package com.monsterbank.ms_account.operations.operationDTOs;

import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.enums.OperationSide;
import java.math.BigDecimal;

public class OperationDTO {

    private String type = "operation";
    private OperationSide side;
    private String accNumber;
    private BigDecimal value;
    private String datetime;

    //CONSTRUTOR DO JACKSON
    public OperationDTO(){};

    //CONSTRUTOR QUE O EXTRATODTO USA PRA CRIA AS LISTAS COM BASE NO CONTEUDO DE OPERS DO EXTRATOENTITY
    public OperationDTO(OperationEntity oe){
        this.side = oe.getSide();
        this.accNumber = oe.getAccNumber();
        this.value = oe.getValue();
        this.datetime = oe.getDatetime();
    }

    public OperationDTO(OperationSide os, String accNumber, BigDecimal value, String datetime){
        this.side = os;
        this.accNumber = accNumber;
        this.value = value;
        this.datetime = datetime;
    }

    //GETTERS E SETTERS SÓ PRO JACKSON NÃO DAR PITI

    public String getType() {
        return type;
    }

    public OperationSide getSide() {
        return side;
    }

    public String getAccNumber() {
        return accNumber;
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

    public void setSide(OperationSide side) {
        this.side = side;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
