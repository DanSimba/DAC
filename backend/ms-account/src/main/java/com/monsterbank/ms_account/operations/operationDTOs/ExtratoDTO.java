package com.monsterbank.ms_account.operations.operationDTOs;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import com.monsterbank.ms_account.operations.ExtratoEntity;

public class ExtratoDTO {

    private Integer dateId;
    private List<OperationEntity> opers;
    private List<TransfereceEntity> transfs;
    private String accNumber;
    private BigDecimal saldoApos;

    public ExtratoDTO(
        Integer dateId,
        String accNumber,
        BigDecimal saldoApos
    ){
        this.dateId = dateId;

        this.opers = new ArrayList<>();
        this.transfs = new ArrayList<>();

        this.accNumber = accNumber;
        this.saldoApos = saldoApos;
    }

    public ExtratoDTO(ExtratoEntity ee){
         this.dateId = ee.getDateId();

        this.opers = ee.getOpers();
        this.transfs = ee.getTransfs();
        this.accNumber = ee.getAccNumber();
        this.saldoApos = ee.getSaldoApos();
    }
}
