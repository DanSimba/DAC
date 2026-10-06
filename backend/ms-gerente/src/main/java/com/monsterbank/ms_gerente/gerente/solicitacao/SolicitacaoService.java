package com.monsterbank.ms_gerente.gerente.solicitacao;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_gerente.gerente.GerenteEntity;
import com.monsterbank.ms_gerente.gerente.GerenteRepository;
import com.monsterbank.ms_gerente.gerente.solicitacao.enumeration.StatusSolicitacao;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SolicitacaoService {
    private static final Logger log = LoggerFactory.getLogger(SolicitacaoService.class);

    private final GerenteRepository gerenteRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    SolicitacaoService(GerenteRepository gerenteRepository, SolicitacaoRepository solicitacaoRepository) {
        this.gerenteRepository = gerenteRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Transactional 
    public void registraAvaliacaoPendente(String cpf, String nome, String salario) {
        log.info("Iniciando registro de solicitação pendente para um gerente");

        GerenteEntity gerente = gerenteRepository.findFirstByOrderByQuantidadeClientesVinculadosAsc().orElseThrow(() -> new RuntimeException("Nenhum gerente disponível no sistema."));
        log.info("ID gerente: {}", gerente.getId());
        log.info("Nome: {}", gerente.getNome());
        log.info("Quantidade de clientes antes de vincular: {}", gerente.getQuantidadeClientesVinculados());

        SolicitacaoEntity solicitacao = new SolicitacaoEntity();
        solicitacao.setCpfCliente(cpf);
        solicitacao.setNomeCliente(nome);
        solicitacao.setSalarioCliente(salario);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setGerenteResponsavel(gerente);

        solicitacaoRepository.save(solicitacao);
        log.info("Solicitação salva.");
        log.info("CPF do cliente: {}", solicitacao.getCpfCliente());
        log.info("Nome do cliente: {}", solicitacao.getNomeCliente());
        log.info("Status da solicitação: {}", solicitacao.getStatus());

        gerente.setQuantidadeClientesVinculados(gerente.getQuantidadeClientesVinculados() + 1);
        gerenteRepository.save(gerente);
        log.info("Quantidade de clientes depois de vincular: {}", gerente.getQuantidadeClientesVinculados());
    }

    public void aprovarSolicitacao() {
        

    }

    public void reprovarSolicitacao() {
        
    }
}
