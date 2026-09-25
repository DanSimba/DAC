package com.monsterbank.ms_account.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.exceptions.ErroCriacaoAccountException;
import com.monsterbank.ms_account.operations.ExtratoEntity;
import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.enums.OperationSide;
import com.monsterbank.ms_account.operations.operationDTOs.ExtratoDTO;
import com.monsterbank.ms_account.operations.ExtratoRepository;

import java.math.BigDecimal;
import java.nio.file.OpenOption;
import java.sql.Date;

import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ExtratoRepository extratoRepository;

    public AccountService(AccountRepository accountRepository, ExtratoRepository extratoRepository) {
        this.accountRepository = accountRepository;
        this.extratoRepository = extratoRepository;
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

    public ExtratoDTO operate(OperationEntity op){
        try{
            //MUDAR BALANCO
            AccountEntity acc = this.accountRepository.findByNumber(op.getAccNumber());
            BigDecimal novoBalanco = BigDecimal.ZERO;
            if(op.getSide() == OperationSide.DEP){

               novoBalanco = acc.getBalanco().add(op.getValue());
               acc.setBalanco(novoBalanco);
            } else{

                //**************
                //PERGUNTAR PRO RAZER: A VERIFICAÇÃO SE TEM SALDO O SUFICIENTE PODE FICAR SÓ NO FRONT? OU EU VOU TER Q FZR ESSA MERDA DNV AQUI?
                //
                novoBalanco = acc.getBalanco().subtract(op.getValue());
                acc.setBalanco(novoBalanco);
            }

            this.accountRepository.save(acc);
            
            //CRIA EXTRATO
            //cria id com base na data atual
                Date now = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                Integer nowId =  Integer.parseInt(formatter.format(now));
            
            //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
            ExtratoEntity ext =  this.extratoRepository.findByDateId(nowId)
                .orElseGet(() -> new ExtratoEntity(
                    nowId,
                    acc.getNumber(),
                    novoBalanco
                ));

            ext.addOperation(op);
            this.extratoRepository.save(ext);

            ExtratoDTO extDto = new ExtratoDTO(ext);
            return extDto;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro realizar operação", e);
        }
    }
    
}
