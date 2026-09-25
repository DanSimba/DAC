package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.enums.OperationSide;


@Entity
@Table(name = "transference")
public class TransferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "extrato_id")
    private ExtratoEntity extrato;

    @Column(nullable = false)
    private String type = "transference";

    @Column(nullable = false)
    private String cpfOrigin;

    @Column(nullable = false)
    private String nameOrigin;

    @Column(nullable = false) //AMBOS OS ACCS SÃO O NUMERO DA CONTA
    private String accOrigin;

    @Column(nullable = false)
    private String accDestiny;

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private String datetime;

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
}
