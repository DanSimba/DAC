package com.monsterbank.ms_cliente.cliente;


import com.monsterbank.ms_cliente.exception.ClienteNaoEncontradoException;
import com.monsterbank.ms_cliente.exception.ErroCriacaoClienteException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoEncontradaException;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoRepository;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ClienteService {
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired 
    private SolicitacaoRepository solicitacaoRepository;

    public List<ClienteRetorno> listarClientes(String field, Integer page){
        PageRequest peageable = PageRequest.of(page, 50);
        if(field == null || field.isBlank()){
            List<ClienteEntity> lista = clienteRepository.findAll(peageable).getContent();

            return lista.stream()
                    .map(cliente -> new ClienteRetorno(
                            cliente.getCpf(),
                            cliente.getNome(),
                            cliente.getCidade(),
                            cliente.getUf(),
                            getSalario(cliente.getCpf())
                    )).toList();

        }

        return clienteRepository.findByCpfContainingOrNomeContaining(field, field, peageable)
                .map(cliente -> new ClienteRetorno(
                        cliente.getCpf(),
                        cliente.getNome(),
                        cliente.getCidade(),
                        cliente.getUf(),
                        getSalario(cliente.getCpf())
                )).toList();

    }


    @Transactional 
    public void criarCliente(String cpf){
        log.info("{} --- Iniciando criação de cliente para o CPF: {}", ClienteService.class, cpf);

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
        }catch(ErroCriacaoClienteException e){
            throw new RuntimeException("Erro ao criar cliente", e);
        }
    }

    public void removerCliente(String cpf){
        ClienteEntity cliente = clienteRepository.findByCpf(cpf).orElseThrow(() -> new ClienteNaoEncontradoException());

        clienteRepository.delete(cliente);
    }


    private String getSalario(String cpf){
        //consulta conta
        return "12312321";
    }

    public String findNameByCpf(String cpf){
        ClienteEntity cliente = clienteRepository.findByCpf(cpf)
        .orElseThrow(() -> new ClienteNaoEncontradoException());

        return cliente.getCpf();
    }
    
    public ClienteRetorno findByCpf(String cpf){
        ClienteEntity cliente = clienteRepository.findByCpf(cpf)
        .orElseThrow(() -> new ClienteNaoEncontradoException());

        return new ClienteRetorno(
                        cliente.getCpf(),
                        cliente.getNome(),
                        cliente.getCidade(),
                        cliente.getUf(),
                        getSalario(cliente.getCpf())
                );
    }
}
