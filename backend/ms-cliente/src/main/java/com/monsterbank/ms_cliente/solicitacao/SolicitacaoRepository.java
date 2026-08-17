package com.monsterbank.ms_cliente.solicitacao;

import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<SolicitacaoEntity, String> {
}
