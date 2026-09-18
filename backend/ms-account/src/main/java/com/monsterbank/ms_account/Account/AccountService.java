package com.monsterbank.ms_account.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.exceptions.ErroCriacaoAccountException;

import java.math.BigDecimal;


import java.util.Optional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<AccountDTO> findAccountByCpf(String cpf){
        return this.accountRepository.findByClientCpf(cpf).map(
            acc -> new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId()
            )
        );
    }

    public Optional<AccountDTO> findAccountByNumber(String number){
        return this.accountRepository.findByNumber(number).map(
            acc -> new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId()
            )
        );
    }

    public AccountDTO createAccount(String cpf, Long managerId){
        try{
            AccountEntity acc = new AccountEntity(
                cpf,
                BigDecimal.ZERO, 
                managerId
            );

            this.accountRepository.save(acc);

            AccountDTO accDto = new AccountDTO(acc.getNumber(), acc.getClientCpf(),acc.getBalanco(), acc.getManagerId());
            return accDto;
            
        } catch (ErroCriacaoAccountException e) {
            throw new RuntimeException("Erro ao criar conta", e);
        }
    }
}
