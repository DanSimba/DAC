package com.monsterbank.ms_cliente.solicitacao;

import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SolicitacaoRepository extends JpaRepository<SolicitacaoEntity, String> {

    Optional<SolicitacaoEntity> findByCpf(String cpf);
    Optional<SolicitacaoEntity> findByEmail(String email);
}
