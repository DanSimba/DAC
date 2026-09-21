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
    private List<OperationEntity> opers = new ArrayList<>();;

    @OneToMany(mappedBy = "extrato")
    private List<TransfereceEntity> transfs = new ArrayList<>();;

    @Column(nullable = false)
    private String acc_number;

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal saldoApos;
}
