package com.monsterbank.ms_account.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;

import com.monsterbank.ms_account.exceptions.ErroCriacaoAccountException;
import com.monsterbank.ms_account.exceptions.SaldoInsuficienteException;

import com.monsterbank.ms_account.operations.ExtratoEntity;
import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;
import com.monsterbank.ms_account.operations.enums.OperationSide;
import com.monsterbank.ms_account.operations.operationDTOs.ExtratoDTO;
import com.monsterbank.ms_account.operations.ExtratoRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import java.math.BigDecimal;
import java.nio.file.OpenOption;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ExtratoRepository extratoRepository;

    public AccountService(AccountRepository accountRepository, ExtratoRepository extratoRepository) {
        this.accountRepository = accountRepository;
        this.extratoRepository = extratoRepository;
    }

    public AccountDTO getMockAcc(){
        BigDecimal bal = new BigDecimal("1100.45");
        AccountDTO acc = new AccountDTO(
                "999",
                "11122233399",
                bal,
                100L
            );
        return acc;
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
            
        } catch (Exception e) {
            throw new ErroCriacaoAccountException();
        }
    }

    public ExtratoDTO operate(OperationEntity op){
            //MUDAR BALANCO
            AccountEntity acc = this.accountRepository
            .findByNumber(op.getAccNumber())
            .orElseThrow(() -> new RuntimeException("Conta não encontrada!!!"));

            BigDecimal novoBalanco = BigDecimal.ZERO;
            if(op.getSide() == OperationSide.DEP){

               novoBalanco = acc.getBalanco().add(op.getValue());
               acc.setBalanco(novoBalanco);
            } else{

                //**************
                //PERGUNTAR PRO RAZER: A VERIFICAÇÃO SE TEM SALDO O SUFICIENTE PODE FICAR SÓ NO FRONT? OU EU VOU TER Q FZR ESSA MERDA DNV AQUI?
                //
                novoBalanco = acc.getBalanco().subtract(op.getValue());
                //compareTo retorna -1 se for menor***
                if(novoBalanco.compareTo(BigDecimal.ZERO)<0)throw new SaldoInsuficienteException();
                acc.setBalanco(novoBalanco);
            }

            this.accountRepository.save(acc);
            
            //CRIA EXTRATO
            //cria id com base na data atual
                Date now = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                Integer nowId =  Integer.parseInt(formatter.format(now));
            
            //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
            final BigDecimal balancoFinal = novoBalanco; //a func de flecha obriga que o valor seja imutavel
            ExtratoEntity ext =  this.extratoRepository.findByDateIdAndAccNumber(nowId, op.getAccNumber())
                .orElseGet(() -> new ExtratoEntity(
                    nowId,
                    acc.getNumber(),
                    balancoFinal
                ));

            ext.addOperation(op);
            this.extratoRepository.save(ext);

            ExtratoDTO extDto = new ExtratoDTO(ext);
            return extDto;
    }
    

    public ExtratoDTO transfer(TransferenceEntity t){
            //ENCONTRAR AMBAS AS CONTAS
                AccountEntity originAcc = this.accountRepository
                .findByNumber(t.getAccOrigin())
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada!!!"));

                AccountEntity destinyAcc = this.accountRepository
                .findByNumber(t.getAccDestiny())
                .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada!!!"));

            //REMOVE E ADICIONA VALORES
                //VERIFICA SE TEM SALDO: SE N TEM RETORNA EXCEPTION
                    BigDecimal novoBalancoOrigin = originAcc.getBalanco().subtract(t.getValue());
                    if(novoBalancoOrigin.compareTo(BigDecimal.ZERO)<0) throw new SaldoInsuficienteException();
                    originAcc.setBalanco(novoBalancoOrigin);

                BigDecimal novoBalancoDestiny = destinyAcc.getBalanco().add(t.getValue());
                destinyAcc.setBalanco(novoBalancoDestiny);
            
            this.accountRepository.save(destinyAcc);
            this.accountRepository.save(originAcc);
            
            //CRIA EXTRATO
            //cria id com base na data atual
                Date now = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                Integer nowId =  Integer.parseInt(formatter.format(now));
            
            //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
            final BigDecimal balancoFinalOrigin = novoBalancoOrigin;
                ExtratoEntity extOrigin =  this.extratoRepository.findByDateIdAndAccNumber(nowId, originAcc.getNumber())
                    .orElseGet(() -> new ExtratoEntity(
                        nowId,
                        originAcc.getNumber(),
                        balancoFinalOrigin
                    ));

                extOrigin.addTransference(t);
            this.extratoRepository.save(extOrigin);

            //CRIA NOVO EXTRATO DO DESTINATARIO
            final BigDecimal balancoFinalDestiny = novoBalancoDestiny;
            TransferenceEntity destinyTransf = new TransferenceEntity(originAcc.getNumber(), destinyAcc.getNumber(), t.getValue(), now);
            ExtratoEntity extDestiny =  this.extratoRepository.findByDateIdAndAccNumber(nowId, destinyAcc.getNumber())
                    .orElseGet(() -> new ExtratoEntity(
                        nowId,
                        destinyAcc.getNumber(),
                        balancoFinalDestiny
                    ));

            extDestiny.addTransference(destinyTransf);
            this.extratoRepository.save(extDestiny);

            ExtratoDTO extDto = new ExtratoDTO(extOrigin);
            return extDto;
    }

    public List<ExtratoDTO> listExtratos(String number){
        List<ExtratoEntity> exts = this.extratoRepository.findByAccNumber(number);

        return exts.stream().map( //pega a lista e tranforma um por um em DTO
            ext -> new ExtratoDTO(ext) 
        ).collect(Collectors.toList());
    }
}
