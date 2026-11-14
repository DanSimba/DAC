package com.monsterbank.ms_account.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monsterbank.ms_account.query.entities.EventReadEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventReadRepository extends JpaRepository<EventReadEntity, Long> {

    //ENCONTRA LISTA DE EVENTOS DA VERSÃO MAIS ANTIGA ATÉ MAIS RECENTE
    List<EventReadEntity> findByObjetoIdOrderByVersaoAsc(String objetoId);

    //ENCONTRA EVENTO E VAI DIRETO PRA VERSÃO MAIS RECENTE (TOP + DESC)
    Optional<EventReadEntity> findTopByObjetoIdOrderByVersaoDesc(String objetoId);
}
