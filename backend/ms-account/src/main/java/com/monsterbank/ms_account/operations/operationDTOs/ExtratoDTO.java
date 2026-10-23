package com.monsterbank.ms_account.operations.operationDTOs;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.monsterbank.ms_account.operations.ExtratoEntity;

import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;

public class ExtratoDTO {

    private Integer dateId;
    private List<OperationDTO> opers;
    private List<TransferenceDTO> transfs;
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
        this.opers = ee.getOpers().stream().map(OperationDTO::new).collect(Collectors.toList());
        this.transfs = ee.getTransfs().stream().map(TransferenceDTO::new).collect(Collectors.toList());
        this.accNumber = ee.getAccNumber();
        this.saldoApos = ee.getSaldoApos();
    }

    public Integer getDateId() {
        return dateId;
    }

    public List<OperationDTO> getOpers() {
        return opers;
    }

    public List<TransferenceDTO> getTransfs() {
        return transfs;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public BigDecimal getSaldoApos() {
        return saldoApos;
    }
}
