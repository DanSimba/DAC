package com.monsterbank.ms_account.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monsterbank.ms_account.operations.ExtratoEntity;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExtratoRepository extends JpaRepository<ExtratoEntity, String>{
    
    Optional<ExtratoEntity> findByDateId(Integer dateId);

    List<ExtratoEntity> findByAccNumber(String accNumber);
}
