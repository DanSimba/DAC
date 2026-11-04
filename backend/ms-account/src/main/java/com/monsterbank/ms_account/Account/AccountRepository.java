package com.monsterbank.ms_account.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monsterbank.ms_account.Account.AccountEntity;
import com.monsterbank.ms_account.operations.ExtratoEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    Optional<AccountEntity> findByNumber(String number);
    
    Optional<AccountEntity> findByClientCpf(String clientCpf);

    List<AccountEntity> findAll();
}
