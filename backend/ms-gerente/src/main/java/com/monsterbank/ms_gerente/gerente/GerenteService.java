package com.monsterbank.ms_gerente.gerente;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.monsterbank.ms_gerente.gerente.solicitacaoAvaliacao.SolicitacaoAvaliacaoEntity;
import com.monsterbank.ms_gerente.gerente.solicitacaoAvaliacao.SolicitacaoAvaliacaoRepository;
import com.monsterbank.ms_gerente.gerente.solicitacaoAvaliacao.enumeration.StatusSolicitacao;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GerenteService {
    private static final Logger log = LoggerFactory.getLogger(GerenteService.class);

    private final GerenteRepository gerenteRepository;
    private final SolicitacaoAvaliacaoRepository solicitacaoRepository;

    GerenteService(GerenteRepository gerenteRepository, SolicitacaoAvaliacaoRepository solicitacaoRepository) {
        this.gerenteRepository = gerenteRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public GerenteDTO paraDTO(GerenteEntity gerente) {
        GerenteDTO respostaDTO = new GerenteDTO();
        respostaDTO.setId(gerente.getId());
        respostaDTO.setNome(gerente.getNome());
        respostaDTO.setCpf(gerente.getCpf());
        respostaDTO.setEmail(gerente.getEmail());
        respostaDTO.setTelefone(gerente.getTelefone());
        return respostaDTO;
    }

    public List<GerenteDTO> listar() {
        return gerenteRepository.findAll().stream()
                .filter(GerenteEntity::getAtivo)
                .map(this::paraDTO)
                .collect(Collectors.toList());
    }

    public GerenteDTO buscaId(Long id) {
        GerenteEntity gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        return paraDTO(gerente);
    }

    public GerenteDTO salvar(GerenteDTO gerenteDTO) {
        GerenteEntity gerente = new GerenteEntity();

        gerente.setNome(gerenteDTO.getNome());
        gerente.setCpf(gerenteDTO.getCpf());
        gerente.setEmail(gerenteDTO.getEmail());
        gerente.setTelefone(gerenteDTO.getTelefone());
        gerente.setAtivo(true);

        gerenteRepository.save(gerente);

        return paraDTO(gerente);
    }

    public GerenteDTO atualizar(Long id, GerenteDTO gerenteDTO) {
        GerenteEntity gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        // Lembrando que cpf e email não pode ser alterado
        gerente.setNome(gerenteDTO.getNome());
        gerente.setTelefone(gerenteDTO.getTelefone());

        GerenteEntity gerenteAtualizado = gerenteRepository.save(gerente);
        return paraDTO(gerenteAtualizado);
    }

    public void remover(long id) {
        GerenteEntity gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        gerente.setAtivo(false);
        gerenteRepository.save(gerente);
    }

    @Transactional
    public void registraAvaliacaoPendente(String cpf, String nome, String salario) {
        log.info("Iniciando registro de solicitação pendente para um gerente");

        GerenteEntity gerente = gerenteRepository.findFirstByOrderByQuantidadeClientesVinculadosAsc().orElseThrow(() -> new RuntimeException("Nenhum gerente disponível no sistema."));
        log.info("ID gerente: {}", gerente.getId());
        log.info("Nome: {}", gerente.getNome());
        log.info("Quantidade de clientes antes de vincular: {}", gerente.getQuantidadeClientesVinculados());

        SolicitacaoAvaliacaoEntity solicitacao = new SolicitacaoAvaliacaoEntity();
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
}
