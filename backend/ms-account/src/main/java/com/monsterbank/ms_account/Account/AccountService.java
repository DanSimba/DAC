package com.monsterbank.ms_account.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.exceptions.ErroCriacaoAccountException;

import java.util.List;

public class AccountService {

    @AutoWired
    private AccountRepository accountRepository;

    public findAccountByCpf(String cpf){
        return this.accountRepository.findByCpf(cpf).map(
            acc -> new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
            )
        );
    }

    public findAccountByNumber(String number){
        return this.accountRepository.findByNumber(number).map(
            acc -> new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
            )
        );
    }

    public createAccount(String cpf, Long managerId){
        try{
            AccountEntity acc = new AccountEntity(
                cpf,
                BigDecimal.ZERO, 
                managerId
            );

            this.accountRepository.save(acc);
            
        } catch (ErroCriacaoAccountException e) {
            throw new RuntimeException("Erro ao criar conta", e);
        }
    }


    public String getCurrentTimeFormated() {
        LocalDateTime date = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy H:mm");

        return date.format(formatter);
    }
}
