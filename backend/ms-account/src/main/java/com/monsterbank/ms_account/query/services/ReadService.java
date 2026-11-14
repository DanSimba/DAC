package com.monsterbank.ms_account.query.services;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_account.query.entities.EventReadEntity;
import com.monsterbank.ms_account.query.repositories.EventReadRepository;

import com.monsterbank.ms_account.query.entities.AccountReadEntity;
import com.monsterbank.ms_account.query.repositories.AccountReadRepository;

import com.monsterbank.ms_account.account.accountDTOs.*;

import com.monsterbank.ms_account.exceptions.*;

import com.monsterbank.ms_account.events.eventDTOs.*;
import com.monsterbank.ms_account.events.enums.EventType;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ReadService {

    //COMO TEM POUCO CÓDIGO VALE A PENA SIMPLIFICAR TUDO EM UM SERVICE SÓ
    private final EventReadRepository eventRepository;
    private final AccountReadRepository accountRepository;

    public ReadService(AccountReadRepository arr, EventReadRepository err) {
        this.accountRepository = arr;
        this.eventRepository = err;
    }

    //PARTE DA ACC--------------------------------------------------fgbvbhfvhbffrjnfvjn
    public AccountDTO findAccountByNumber(String accNumber) {
        AccountReadEntity acc = accountRepository.findById(accNumber)
                .orElseThrow(() -> new ErroContaNaoEncontradaException());

        return new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
            );
    }

    public AccountDTO findAccountByCpf(String cpf){
        AccountReadEntity acc = this.accountRepository.findByClientCpf(cpf)
                .orElseThrow(() -> new ErroContaNaoEncontradaException());
       
        return new AccountDTO(
                acc.getNumber(),
                acc.getClientCpf(),
                acc.getBalanco(),
                acc.getManagerId(),
                acc.getCreatedAt()
        );
    }

    public List<AccountReadEntity> findAllAccounts() {
        return accountRepository.findAll();
    }

    //PARTE DOS EVENTOS-----------------------------idhwcbedcbupdkhcb kpwdnk

    //public List<EventReadEntity> findEventsByAccount(String objetoId) {
    //    return eventRepository.findByObjetoIdOrderByVersaoAsc(objetoId);
    //}

    //public EventReadEntity findLastEvent(String objetoId) {
    //    return eventRepository.findTopByObjetoIdOrderByVersaoDesc(objetoId)
    //        .orElseThrow(() -> new EventoNaoEncontrado());
    //}

    public EventReadEntity saveEvent(EventReadEntity event) {
        return eventRepository.save(event);
    }

    public AccountLastVersion getLastVersion(String accNumber){
        //PEGA ULTIMO EVENTO (PRA PREENCHER DATA)
        EventDTO le = this.getLastEvent(accNumber);
        LocalDateTime lastUpdt = le.getDatetime();

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String lastUpdtString = lastUpdt.format(formatador);

        BigDecimal balancoAtual = this.getBalancoAtual(accNumber);

        Long lastGerente = this.getLastGerente(accNumber);

        AccountLastVersion acl = new AccountLastVersion(balancoAtual, lastGerente, lastUpdtString);

        return acl;
    }

    public BigDecimal getBalancoAtual(String accNumber){
        //ENCONTRA A LISTA
        List<EventReadEntity> eventos = eventRepository.findByObjetoIdOrderByVersaoAsc(accNumber);

        BigDecimal balanco = BigDecimal.ZERO;

        for (EventReadEntity evento : eventos) {
            System.out.println(
                "EVENTO: " + evento.getTipo()
                + " | VERSAO: " + evento.getVersao()
                + " | PAYLOAD: " + evento.getPayload()
            );
            switch (evento.getTipo()) { 
                case DEPOSITO:
                    balanco = balanco.add( //SÓ PROCURA 'VALOR' NO PAYLOAD SE FOR UM DESSES TIPOS, EVITA PROCURAR O VALOR CASO ELE NÃO EXISTA NO PAYLOAD (TAVA DANDO NULLPOINTEREXCEPTION)
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                case TRANSFERENCIA_DESTINO:
                    balanco = balanco.add(
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                case SAQUE:
                    balanco = balanco.subtract(
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                case TRANSFERENCIA_ORIGEM:
                    balanco = balanco.subtract(
                        evento.getPayload().get("valor").decimalValue()
                    );
                    break;
                default:
                    break;
            }
        }
        return balanco;
    }

    public Long getLastGerente(String accNumber){ //RETORNA CPF DO DO GERENTE ATUAL
          List<EventReadEntity> eventos = eventRepository.findByObjetoIdOrderByVersaoAsc(accNumber);

          //NÃO PODE ESTAR VAZIO PQ TEM Q TER PELO MENOS O CRIADO
            if (eventos.isEmpty()) throw new EventoNaoEncontrado();

            Long gerCpf = null;

            for (EventReadEntity evento : eventos) {
                if (evento.getTipo() == EventType.CRIADO||evento.getTipo() == EventType.GERENTE_ALTERADO) {

                    var payload = evento.getPayload();//pega payload
                    var cpfNode = payload.get("cpfGerente");//pega campo cpfgerente

                    String cpfTexto = cpfNode.asText();//transforma em string, pq aparentemente é mto dificil pro javinha tranformar de payload pra Long direto

                    gerCpf = Long.parseLong(cpfTexto); //tranforma em Long

                    //System.out.println("CPF CONVERTIDO: " + gerCpf);
                }
            }

            if (gerCpf == null) throw new EventoNaoEncontrado();

            return gerCpf;
    }

    public EventDTO getLastEvent(String accNumber){
        EventReadEntity lastEvent = this.eventRepository.findTopByObjetoIdOrderByVersaoDesc(accNumber)
        .orElseThrow(() -> new EventoNaoEncontrado());

        EventDTO leDTO = new EventDTO(
            lastEvent.getId(),
            lastEvent.getObjetoId(),
            lastEvent.getTipo(),
            lastEvent.getPayload(),
            lastEvent.getVersao(),
            lastEvent.getDatetime()
        );

        return leDTO;
    }
}
