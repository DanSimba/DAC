package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.enums.OperationSide;


@Entity
@Table(name = "operation")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "extrato_id")
    private ExtratoEntity extrato;

    @Column(nullable = false)
    private String type = "operation";

    @Column(nullable = false)
    private String accNumber;

    @Enumerated(EnumType.STRING) //GUARDA COMO STRING PQ NGM MERECE NÉ
    @Column(nullable = false)
    private OperationSide side;

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private String datetime;

    public OperationSide getSide() {
        return this.side;
    }

    public String getAccNumber() {
        return this.acc_number;
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
}
