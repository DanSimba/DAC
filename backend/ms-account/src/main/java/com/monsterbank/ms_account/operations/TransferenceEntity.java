package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "transference")
public class TransferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "extrato_id")
    @JsonIgnore //ISSO PROIBE O JACKSON DE BUSCAR O EXTRATO ORIGINAL, QUE BUSCA A OPER, QUE BUSCA O EXTRATO DNV E GERA UM LOOP
    private ExtratoEntity extrato;

    @Column(name = "type", nullable = false)
    private String type = "transference";

    @Column(name = "cpf_origin", nullable = false)
    private String cpfOrigin;

    @Column(name = "name_origin", nullable = false)
    private String nameOrigin;

    @Column(name = "acc_origin", nullable = false) //AMBOS OS ACCS SÃO O NUMERO DA CONTA
    private String accOrigin;

    @Column(name = "acc_destiny", nullable = false)
    private String accDestiny;

    @Column(name = "value", nullable = false, precision = 17, scale = 2)
    private BigDecimal value;

    @Column(name = "datetime", nullable = false)
    private String datetime; //DIFERENTE DE DATEID

    public TransferenceEntity(){}//CONSTRUTOR QUE O HIBERNATE USA PARA DEVOLVER OQ EU BUSCO DO REPO

    public Long getId() {
        return this.id;
    }

    public ExtratoEntity getExtrato() {
        return this.extrato;
    }

    public String getType() {
        return this.type;
    }

    public String getCpfOrigin() {
        return this.cpfOrigin;
    }

    public String getNameOrigin() {
        return this.nameOrigin;
    }

    public String getAccOrigin() {
        return this.accOrigin;
    }

    public String getAccDestiny() {
        return this.accDestiny;
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
