package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "extrato")
public class ExtratoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer dateId;

    @OneToMany(mappedBy = "extrato")
    private List<OperationEntity> opers;

    @OneToMany(mappedBy = "extrato")
    private List<TransfereceEntity> transfs;

    @Column(nullable = false)
    private String accNumber;

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal saldoApos;

     public ExtratoEntity(
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

    public Long getId() {
        return this.id;
    }

    public Integer getDateId() {
        return this.dateId;
    }

    public List<OperationEntity> getOpers() {
        return this.opers;
    }

    public List<TransfereceEntity> getTransfs() {
        return this.transfs;
    }

    public String getAccNumber() {
        return this.accNumber;
    }

    public BigDecimal getSaldoApos() {
        return this.saldoApos;
    } 

    public void addOperation(OperationEntity op){
        this.opers.add(op);
    }

    public void addTransference(TransferenceEntity t){
        this.transfs.add(t);
    }
}
