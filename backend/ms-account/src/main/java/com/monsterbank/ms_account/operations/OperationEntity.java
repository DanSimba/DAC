package com.monsterbank.ms_account.operations;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.monsterbank.ms_account.operations.enums.OperationSide;


@Entity
@Table(name = "operation")
public class OperationEntity {

    @ManyToOne
    @JoinColumn(name = "extrato_id")
    private ExtratoEntity extrato;

    @Column(nullable = false)
    private String type = "operation";

    @Column(nullable = false)
    private String acc_number;

    @Column(nullable = false)
    private OperationSide side;

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private String datetime;
}
