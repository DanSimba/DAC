package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.operationDTOs.OperationDTO;

import com.monsterbank.ms_account.operations.enums.OperationSide;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "operation")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "extrato_id")
    @JsonIgnore //ISSO PROIBE O JACKSON DE BUSCAR O EXTRATO ORIGINAL, QUE BUSCA A OPER, QUE BUSCA O EXTRATO DNV E GERA UM LOOP
    private ExtratoEntity extrato;

    @Column(name = "type", nullable = false)
    private String type = "operation";

    @Column(name = "acc_number",nullable = false)
    private String accNumber;

    @Enumerated(EnumType.STRING) //GUARDA COMO STRING PQ NGM MERECE NÉ
    @Column(name = "side", nullable = false)
    private OperationSide side;

    @Column(name = "value", nullable = false, precision = 17, scale = 2)
    private BigDecimal value;

    @Column(name = "datetime", nullable = false)
    private String datetime; //DD/MM/AAAA hh:mm

    public OperationEntity(){}//CONSTRUTOR QUE O HIBERNATE USA PARA DEVOLVER OQ EU BUSCO DO REPO

    public OperationEntity(OperationDTO op){
        this.type = "operation";
        this.side = op.getSide();
        this.accNumber = op.getAccNumber();
        this.value = op.getValue();
        this.datetime = op.getDatetime();
    }

    public OperationSide getSide() {
        return this.side;
    }

    public String getAccNumber() {
        return this.accNumber;
    }

    public Long getId() {
        return this.id;
    }

    public ExtratoEntity getExtrato() {
        return this.extrato;
    }

    public String getType() {
        return this.type;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public String getDatetime() {
        return this.datetime;
    }

    public void setExtrato(ExtratoEntity extrato) {
        this.extrato = extrato;
    }
}
