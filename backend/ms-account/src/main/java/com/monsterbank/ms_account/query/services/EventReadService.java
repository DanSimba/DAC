package com.monsterbank.ms_account.query.services;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.query.entities.EventReadEntity;
import com.monsterbank.ms_account.query.repositories.EventReadRepository;

import com.monsterbank.ms_account.exceptions.*;

import java.util.List;

@Service
public class EventReadService {

    private final EventReadRepository repository;

    public EventReadService(EventReadRepository repository) {
        this.repository = repository;
    }

    public List<EventReadEntity> findEventsByAccount(String objetoId) {
        return repository.findByObjetoIdOrderByVersaoAsc(objetoId);
    }

    public EventReadEntity findLastEvent(String objetoId) {
        return repository.findTopByObjetoIdOrderByVersaoDesc(objetoId)
            .orElseThrow(() -> new EventoNaoEncontrado());
    }

    // Persiste um evento recebido
    public EventReadEntity saveEvent(EventReadEntity event) {
        return repository.save(event);
    }
}
