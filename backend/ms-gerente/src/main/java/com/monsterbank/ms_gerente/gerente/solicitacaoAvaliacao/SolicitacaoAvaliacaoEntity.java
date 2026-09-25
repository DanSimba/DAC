package com.monsterbank.ms_gerente.gerente.solicitacaoAvaliacao;

import com.monsterbank.ms_gerente.gerente.GerenteEntity;
import com.monsterbank.ms_gerente.gerente.solicitacaoAvaliacao.enumeration.StatusSolicitacao;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gerente_solicitacao_avaliacao")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoAvaliacaoEntity {
    
    @Id
    private String cpfCliente;
    private String nomeCliente;
    private String salarioCliente;
    private StatusSolicitacao status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id")
    private GerenteEntity gerenteResponsavel;
}
