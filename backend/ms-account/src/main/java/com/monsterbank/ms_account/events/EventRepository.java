package com.monsterbank.ms_account.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    
    //ENCONTRA EVENTO DA VERSÃO MAIS ANTIGA ATÉ MAIS RECENTE
    List<EventEntity> findByObjetoIdOrderByVersaoAsc(String objetoId);

    //ENCONTRA EVENTO E VAI DIRETO PRA VERSÃO MAIS RECENTE (TOP + DESC)
    Optional<EventEntity> findTopByObjetoIdOrderByVersaoDesc(String objetoId);
}
