package com.monsterbank.ms_account.query.services;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.exceptions.*;

import com.monsterbank.ms_account.query.entities.AccountReadEntity;
import com.monsterbank.ms_account.query.repositories.AccountReadRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class AccountReadService {

    private final AccountReadRepository accountRepository;

    public AccountReadService(AccountReadRepository repository) {
        this.accountRepository = repository;
    }

    public AccountReadEntity getAccount(String accNumber) {
        return accountRepository.findById(accNumber)
                .orElseThrow(() -> new ErroContaNaoEncontradaException());
    }

    public List<AccountReadEntity> findAllAccounts() {
        return accountRepository.findAll();
    }
}