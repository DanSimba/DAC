package com.monsterbank.ms_cliente.cliente;


import com.monsterbank.ms_cliente.exception.ErroCriacaoClienteException;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SolicitacaoService solicitacaoService;

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



    private void criarCliente(String cpf){

        SolicitacaoEntity solicitacao = solicitacaoService.getSolicitacaoByCpf(cpf);

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

            clienteRepository.save(cliente);
        }catch(ErroCriacaoClienteException e){
            throw new RuntimeException("Erro ao criar cliente", e);
        }
    }

    private String getSalario(String cpf){
        //consulta conta
        return "12312321";
    }


}
