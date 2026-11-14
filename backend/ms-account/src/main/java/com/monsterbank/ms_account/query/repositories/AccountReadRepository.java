package com.monsterbank.ms_account.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monsterbank.ms_account.query.entities.AccountReadEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountReadRepository extends JpaRepository<AccountReadEntity, String> {

        Optional<AccountReadEntity> findByNumber(String number);
        Optional<AccountReadEntity> findByClientCpf(String clientCpf);
}