package com.monsterbank.ms_account.account;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.account.accountDTOs.AccountLastVersion;
import com.monsterbank.ms_account.exceptions.ErroContaNaoEncontradaException;
import com.monsterbank.ms_account.exceptions.ErroCriacaoAccountException;
import com.monsterbank.ms_account.exceptions.ErroListaDeGerenteVaziaOuNula;
import com.monsterbank.ms_account.exceptions.SaldoInsuficienteException;
import com.monsterbank.ms_account.exceptions.MalabarismoException;
import com.monsterbank.ms_account.exceptions.NullTransactionException;
import com.monsterbank.ms_account.operations.ExtratoEntity;
import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;
import com.monsterbank.ms_account.operations.enums.OperationSide;
import com.monsterbank.ms_account.operations.operationDTOs.*;
import com.monsterbank.ms_account.operations.ExtratoRepository;
import com.monsterbank.ms_account.events.eventDTOs.EventDTO;
import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.events.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import java.math.BigDecimal;
import java.nio.file.OpenOption;

import java.util.Date;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.hibernate.Hibernate;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ExtratoRepository extratoRepository;
    private final EventService eventService;

    public AccountService(AccountRepository accountRepository, ExtratoRepository extratoRepository, EventService eventService) {
        this.accountRepository = accountRepository;
        this.extratoRepository = extratoRepository;
        this.eventService = eventService;
    }

    public Optional<AccountDTO> getMockAcc(){
        //BigDecimal bal = new BigDecimal("1100.45");
        //AccountDTO acc = new AccountDTO(
        //        "999",
        //        "11122233399",
        //        bal,
        //        100L
        //    );
        return this.accountRepository.findByNumber("001").map(
            acc -> new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
            )
        );
    }

    public List<AccountDTO> listaContas(){
        return accountRepository.findAll().stream().map(
            acc -> new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt())
        ).toList();
    }

    public AccountDTO findAccountByCpf(String cpf){
        AccountEntity acc = this.accountRepository.findByClientCpf(cpf)
                .orElseThrow(() -> new ErroContaNaoEncontradaException());
       
        return new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
            );
    }

    public AccountDTO findAccountByNumber(String number){
        AccountEntity acc = this.accountRepository.findByNumber(number)
                .orElseThrow(() -> new ErroContaNaoEncontradaException());
       
        return new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
            );
    }

    public AccountLastVersion getLastVersion(String accNumber){
        //PEGA ULTIMO EVENTO (PRA PREENCHER DATA)
        EventDTO le = this.eventService.getLastEvent(accNumber);
        LocalDateTime lastUpdt = le.getDatetime();

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String lastUpdtString = lastUpdt.format(formatador);

        BigDecimal balancoAtual = this.eventService.getBalancoAtual(accNumber);

        Long lastGerente = this.eventService.getLastGerente(accNumber);

        AccountLastVersion acl = new AccountLastVersion(balancoAtual, lastGerente, lastUpdtString);

        return acl;
    }
  
      public Long findLeastBusyManager(List<Long> idsGerentesAtivos) {
        if (idsGerentesAtivos == null || idsGerentesAtivos.isEmpty()) {
            throw new ErroListaDeGerenteVaziaOuNula();
        }

        Map<Long, Long> contasPorGerente = new HashMap<>();
        for (Long id : idsGerentesAtivos) {
            contasPorGerente.put(id, 0L);
        }

        List<Object[]> contagemPorGerenteAtivo = accountRepository.countContasPorGerente(idsGerentesAtivos);

        for (Object[] row : contagemPorGerenteAtivo) {
            Long gerenteId = (Long) row[0];
            Long contaId = (Long) row[1];
            contasPorGerente.put(gerenteId, contaId);
        }

        return contasPorGerente.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(contasPorGerente.get(0));

    }

    public AccountDTO createAccount(String cpf, Long managerId){
        try{
            AccountEntity acc = new AccountEntity(
                cpf,
                BigDecimal.ZERO, 
                managerId
            );

            this.accountRepository.save(acc);

            //CRIA EVENTO CRIADO
            this.eventService.createCreateEvent(acc.getNumber(), managerId);

            AccountDTO accDto = new AccountDTO(acc.getNumber(), acc.getClientCpf(),acc.getBalanco(), acc.getManagerId(), acc.getCreatedAt());
            return accDto;
            
        } catch (Exception e) {
            throw new ErroCriacaoAccountException();
        }
    }

    @Transactional
    public ExtratoDTO sacar(OperationDTO op){
         //CHECAR SE OP É NULA (VALUE == 0)
            if(op.getValue().compareTo(BigDecimal.ZERO)==0)throw new NullTransactionException();

         //MUDAR BALANCO
            AccountEntity acc = this.accountRepository
            .findByNumber(op.getAccNumber())
            .orElseThrow(() -> new RuntimeException("Conta não encontrada!!!"));

        //CRIA NOVO BALANCO E SETA
            BigDecimal novoBalanco = BigDecimal.ZERO;
            novoBalanco = acc.getBalanco().subtract(op.getValue());
            //compareTo retorna -1 se for menor***
            if(novoBalanco.compareTo(BigDecimal.ZERO)<0)throw new SaldoInsuficienteException();
            acc.setBalanco(novoBalanco);
        
        //CRIA EXTRATO
                LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

                Integer nowId = Integer.parseInt( //NOWID É COM BASE NO HORÁRIO DE SP***
                    hoje.format(DateTimeFormatter.BASIC_ISO_DATE)
                );
            
            //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
                final BigDecimal balancoFinal = novoBalanco; //a func de flecha obriga que o valor seja imutavel
                ExtratoEntity ext =  this.extratoRepository.findByDateIdAndAccNumber(nowId, op.getAccNumber())
                    .orElseGet(() -> new ExtratoEntity(
                        nowId,
                        acc.getNumber(),
                        balancoFinal
                    ));
                
                OperationEntity opEntity = new OperationEntity(op);

                ext.addOperation(opEntity);
                ext.setSaldoApos(balancoFinal); //CASO ELE ACHE O EXTRATO JA EXXISTENTE, ATUALIZA O SLADO MSM ASSIM
                this.extratoRepository.save(ext);

            //garante que a lista seja buscada 
            Hibernate.initialize(ext.getOpers());
            Hibernate.initialize(ext.getTransfs());

            //CRIA EVENTO SACAR
            this.eventService.createSaqueEvent(opEntity, acc.getNumber());

            ExtratoDTO extDto = new ExtratoDTO(ext);
            return extDto;
    }

    @Transactional
    public ExtratoDTO depositar(OperationDTO op){
         //CHECAR SE OP É NULA (VALUE == 0)
            if(op.getValue().compareTo(BigDecimal.ZERO)==0)throw new NullTransactionException();

         //MUDAR BALANCO
            AccountEntity acc = this.accountRepository
            .findByNumber(op.getAccNumber())
            .orElseThrow(() -> new RuntimeException("Conta não encontrada!!!"));

        //CRIA NOVO BALANCO
            BigDecimal novoBalanco = BigDecimal.ZERO;
            novoBalanco = acc.getBalanco().add(op.getValue());
            acc.setBalanco(novoBalanco);
        
        this.accountRepository.save(acc);

        //CRIA EXTRATO
                LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

                Integer nowId = Integer.parseInt( //NOWID É COM BASE NO HORÁRIO DE SP***
                    hoje.format(DateTimeFormatter.BASIC_ISO_DATE)
                );
            
            //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
                final BigDecimal balancoFinal = novoBalanco; //a func de flecha obriga que o valor seja imutavel
                ExtratoEntity ext =  this.extratoRepository.findByDateIdAndAccNumber(nowId, op.getAccNumber())
                    .orElseGet(() -> new ExtratoEntity(
                        nowId,
                        acc.getNumber(),
                        balancoFinal
                    ));
                
                OperationEntity opEntity = new OperationEntity(op);

                ext.addOperation(opEntity);
                ext.setSaldoApos(balancoFinal); //CASO ELE ACHE O EXTRATO JA EXXISTENTE, ATUALIZA O SLADO MSM ASSIM
                this.extratoRepository.save(ext);

                //CRIA O EVENTO PASSANDO A OPERAÇÃO DE ACORDO
                this.eventService.createDepositoEvent(opEntity, acc.getNumber());

            //garante que a lista seja buscada 
            Hibernate.initialize(ext.getOpers());
            Hibernate.initialize(ext.getTransfs());

            ExtratoDTO extDto = new ExtratoDTO(ext);
            return extDto;
    }
    
    // @Transactional
    // public ExtratoDTO operate(OperationDTO op){

    //         //CHECAR SE OP É NULA (VALUE == 0)
    //         if(op.getValue().compareTo(BigDecimal.ZERO)==0)throw new NullTransactionException();

    //         //MUDAR BALANCO
    //         AccountEntity acc = this.accountRepository
    //         .findByNumber(op.getAccNumber())
    //         .orElseThrow(() -> new RuntimeException("Conta não encontrada!!!"));

    //         BigDecimal novoBalanco = BigDecimal.ZERO;
    //         if(op.getSide() == OperationSide.DEP){

    //            novoBalanco = acc.getBalanco().add(op.getValue());
    //            acc.setBalanco(novoBalanco);
    //         } else{
    //             novoBalanco = acc.getBalanco().subtract(op.getValue());
    //             //compareTo retorna -1 se for menor***
    //             if(novoBalanco.compareTo(BigDecimal.ZERO)<0)throw new SaldoInsuficienteException();
    //             acc.setBalanco(novoBalanco);
    //         }

    //         this.accountRepository.save(acc);
            
    //         //CRIA EXTRATO
    //             LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

    //             Integer nowId = Integer.parseInt( //NOWID É COM BASE NO HORÁRIO DE SP***
    //                 hoje.format(DateTimeFormatter.BASIC_ISO_DATE)
    //             );
            
    //         //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
    //             final BigDecimal balancoFinal = novoBalanco; //a func de flecha obriga que o valor seja imutavel
    //             ExtratoEntity ext =  this.extratoRepository.findByDateIdAndAccNumber(nowId, op.getAccNumber())
    //                 .orElseGet(() -> new ExtratoEntity(
    //                     nowId,
    //                     acc.getNumber(),
    //                     balancoFinal
    //                 ));
                
    //             OperationEntity opEntity = new OperationEntity(op);

    //             ext.addOperation(opEntity);
    //             ext.setSaldoApos(balancoFinal); //CASO ELE ACHE O EXTRATO JA EXXISTENTE, ATUALIZA O SLADO MSM ASSIM
    //             this.extratoRepository.save(ext);

    //         //garante que a lista seja buscada 
    //         Hibernate.initialize(ext.getOpers());
    //         Hibernate.initialize(ext.getTransfs());

    //         ExtratoDTO extDto = new ExtratoDTO(ext);
    //         return extDto;
    // }
    

    
    @Transactional
    public ExtratoDTO transfer(TransferenceDTO t){

        //CHECAR SE A TRANSF É NULA (VALUE == 0)
        if(t.getValue().compareTo(BigDecimal.ZERO)==0)throw new NullTransactionException();

        //VERIFICA MALABARISMO
        if(t.getAccOrigin() == t.getAccDestiny())throw new MalabarismoException();

            //ENCONTRAR AMBAS AS CONTAS
                AccountEntity originAcc = this.accountRepository
                .findByNumber(t.getAccOrigin())
                .orElseThrow(() -> new  ErroContaNaoEncontradaException());

                AccountEntity destinyAcc = this.accountRepository
                .findByNumber(t.getAccDestiny())
                .orElseThrow(() -> new ErroContaNaoEncontradaException());

                //seta o cpfdest que não veio do front
                t.setCpfDestiny(destinyAcc.getClientCpf());

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
                LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
                
                //CRIO UM LOCALDATE TIME SÓ NESSE CASO PRA USAR NO CONSTRUTOR DA TRANSF DESTINO
                //LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")); 

                Integer nowId = Integer.parseInt( //NOWID É COM BASE NO HORÁRIO DE SP***
                    hoje.format(DateTimeFormatter.BASIC_ISO_DATE)
                );
            
            //PESQUISA PRA VER SE JÁ EXISTE, SE NÃO CRIA NOVO
            final BigDecimal balancoFinalOrigin = novoBalancoOrigin;
                ExtratoEntity extOrigin =  this.extratoRepository.findByDateIdAndAccNumber(nowId, originAcc.getNumber())
                    .orElseGet(() -> new ExtratoEntity(
                        nowId,
                        originAcc.getNumber(),
                        balancoFinalOrigin
                    ));

                TransferenceEntity tOriginEntity = new TransferenceEntity(t);
                extOrigin.addTransference(tOriginEntity);
                extOrigin.setSaldoApos(balancoFinalOrigin); //CASO ELE ACHE O EXTRATO JA EXXISTENTE, ATUALIZA O SLADO MSM ASSIM
                this.extratoRepository.save(extOrigin);

            //garante que a lista seja buscada 
            Hibernate.initialize(extOrigin.getTransfs());
            Hibernate.initialize(extOrigin.getOpers());

            //CRIA EVENTO TRANSFERENCE_ORIGIN
            this.eventService.createOriginTransferenceEvent(tOriginEntity, originAcc.getNumber());

            //CRIA NOVO EXTRATO DO DESTINATARIO
                final BigDecimal balancoFinalDestiny = novoBalancoDestiny;
                ExtratoEntity extDestiny =  this.extratoRepository.findByDateIdAndAccNumber(nowId, destinyAcc.getNumber())
                        .orElseGet(() -> new ExtratoEntity(
                            nowId,
                            destinyAcc.getNumber(),
                            balancoFinalDestiny
                        ));

                //CRIA UM CLONE DA TRANSFERENCIA MAS MUDA O EXTRATO
                TransferenceEntity tDestinyEntity = new TransferenceEntity(t);
                extDestiny.addTransference(tDestinyEntity);
                extDestiny.setSaldoApos(balancoFinalDestiny); //CASO ELE ACHE O EXTRATO JA EXXISTENTE, ATUALIZA O SLADO MSM ASSIM
                this.extratoRepository.save(extDestiny);
            
            //garante que a lista seja buscada 
            Hibernate.initialize(extDestiny.getTransfs());
            Hibernate.initialize(extDestiny.getOpers());

            //CRIA EVENTO TRANSFERENCE_DESTINY
            this.eventService.createDestinyTransferenceEvent(tDestinyEntity, destinyAcc.getNumber());

            ExtratoDTO extDto = new ExtratoDTO(extOrigin);
            return extDto;
    }

    @Transactional(readOnly = true)
    public List<ExtratoDTO> listExtratos(String number){
        List<ExtratoEntity> exts =
        extratoRepository.findByAccNumber(number);

        for (ExtratoEntity ext : exts) {
            Hibernate.initialize(ext.getOpers());
            Hibernate.initialize(ext.getTransfs());
        }

        return exts.stream()
            .map(ExtratoDTO::new) //mapeia cada item pra DTO (passa o ext como argumento no construtor)
            .collect(Collectors.toList());
    }
}
