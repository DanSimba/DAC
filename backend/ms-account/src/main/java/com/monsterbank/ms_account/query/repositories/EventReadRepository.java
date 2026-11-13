package com.monsterbank.ms_account.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monsterbank.ms_account.query.entities.EventReadEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventReadRepository extends JpaRepository<EventReadEntity, Long> {

    List<EventReadEntity> findByObjetoIdOrderByVersaoAsc(String objetoId);

    //SÓ O MAIS RECENTE
    Optional<EventReadEntity> findTopByObjetoIdOrderByVersaoDesc(String objetoId);
}
