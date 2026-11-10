package com.monsterbank.ms_cliente.cliente;


import com.monsterbank.ms_cliente.exception.ClienteNaoEncontradoException;
import com.monsterbank.ms_cliente.exception.ErroCriacaoClienteException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoEncontradaException;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ClienteService {
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    
    private final ClienteRepository clienteRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final ClienteModelAssembler clienteModelAssembler;

    public ClienteService(
            ClienteRepository clienteRepository,
            SolicitacaoRepository solicitacaoRepository,
            ClienteModelAssembler clienteModelAssembler
    ) {
        this.clienteRepository = clienteRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.clienteModelAssembler = clienteModelAssembler;
    }

    public List<ClienteRetorno> listarClientes(String field, Integer page){
        PageRequest peageable = PageRequest.of(page, 50, Sort.by(Sort.Direction.ASC, "nome"));
        if(field == null || field.isBlank()){
            List<ClienteEntity> lista = clienteRepository.findAll(peageable).getContent();

            return lista.stream()
                    .map(clienteModelAssembler::toModel)
                    .toList();

        }

        return clienteRepository.findByCpfContainingOrNomeContaining(field, field, peageable)
                .map(clienteModelAssembler::toModel)
                .toList();

    }


    @Transactional 
    public void criarCliente(String cpf){
        log.info("{} --- Iniciando criação de cliente para o CPF: {}", ClienteService.class, cpf);

        if (clienteRepository.findByCpf(cpf).isPresent()) {
            return;
        }

        SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());
        log.info("{} --- Solicitação de criação encontrada. solicitacao.getCpf(): {}", ClienteService.class, solicitacao.getCpf());

        try{
            ClienteEntity cliente = new ClienteEntity(
                    solicitacao.getCpf(),
                    solicitacao.getNome(),
                    solicitacao.getEmail(),
                    solicitacao.getTelefone(),
                    solicitacao.getSalario(),
                    solicitacao.getLogradouro(),
                    solicitacao.getNumero(),
                    solicitacao.getComplemento(),
                    solicitacao.getCep(),
                    solicitacao.getCidade(),
                    solicitacao.getUf()
            );

            log.info("{} --- Salvando criação do cliente: CPF {}", ClienteService.class, cliente.getCpf());
            clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ErroCriacaoClienteException();
        }
    }

    @Transactional
    public void removerCliente(String cpf){
        clienteRepository.findByCpf(cpf).ifPresent(clienteRepository::delete);
    }

    public String findNameByCpf(String cpf){
        ClienteEntity cliente = clienteRepository.findByCpf(cpf)
        .orElseThrow(() -> new ClienteNaoEncontradoException());

        return cliente.getNome();
    }
    
    public ClienteRetorno findByCpf(String cpf){
        ClienteEntity cliente = clienteRepository.findByCpf(cpf)
        .orElseThrow(() -> new ClienteNaoEncontradoException());

        return clienteModelAssembler.toModel(cliente);
    }
}
