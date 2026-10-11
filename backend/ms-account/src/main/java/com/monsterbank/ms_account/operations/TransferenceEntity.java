package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.monsterbank.ms_account.operations.enums.OperationSide;


@Entity
@Table(name = "transference")
public class TransferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "extrato_id")
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

    public TransferenceEntity(
        String accOrigin,
        String accDestiny,
        BigDecimal v,
        Date now //como eu vou usar isso na função de tranfer que ja cria uma data de agr, ent eu posso reaproveitar
    ){
        //DEPOIS CONECTAR COM O MS CLIENTE PARA CONSEGUIR ESSES CAMPOS
        //POR ENQUANTO VAMOS TRABALHAR SÓ COM CONTA
        this.cpfOrigin = "";
        this.nameOrigin= "";

        this.accOrigin = accOrigin;
        this.accDestiny = accDestiny;
        this.value = v;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.datetime = formatter.format(now);
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
}
