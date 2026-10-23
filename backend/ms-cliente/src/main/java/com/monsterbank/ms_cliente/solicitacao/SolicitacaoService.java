package com.monsterbank.ms_cliente.solicitacao;

import com.monsterbank.ms_cliente.exception.*;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoHateoasResponse;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.ListarSolicitacoesReturn;
import com.monsterbank.ms_cliente.utils.CpfUtils;
import com.monsterbank.ms_cliente.mensageria.producer.SolicitacaoProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SolicitacaoService {

    private static final Logger log = LoggerFactory.getLogger(SolicitacaoService.class);

    private final SolicitacaoRepository solicitacaoRepository;
    private final SolicitacaoProducer solicitacaoProducer;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository, SolicitacaoProducer solicitacaoProducer) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.solicitacaoProducer = solicitacaoProducer;
    }

    @Transactional
    public SolicitacaoEntity registrar(RegistrarSolicitacaoRequest dto) {
        log.info("{} --- Iniciando registro de solicitação para o CPF: {}", SolicitacaoService.class, dto.cpf());

        validarPorCPF(dto.cpf());
        validarEmailSolicitado(dto.email());
        validarEmailCadastrado(dto.email());

        BigDecimal salario;
        
        try {
            salario = new BigDecimal(dto.salario());
            if(salario.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("{} --- Salario zerado ou negativo para o CPF: {}", SolicitacaoService.class, dto.cpf());
                throw new SalarioInvalidoException(); 
            }
        } catch (NumberFormatException e) {
            log.error("{} --- Erro ao converter salario para BigDecimal: {}, CPF: {}", SolicitacaoService.class, SolicitacaoService.class, dto.salario(), dto.cpf());
            throw new SalarioInvalidoException();
        }

        SolicitacaoEntity solicitacao = new SolicitacaoEntity(
                dto.cpf(),
                dto.nome(),
                dto.email(),
                dto.telefone(),
                salario,
                dto.endereco().logradouro(),
                dto.endereco().numero(),
                dto.endereco().complemento(),
                dto.endereco().cep(),
                dto.endereco().bairro(),
                dto.endereco().cidade(),
                dto.endereco().uf()
                );

        solicitacao = solicitacaoRepository.save(solicitacao);
        log.info("{} --- Solicitacao salva como {} para o CPF: {}", SolicitacaoService.class, solicitacao.getStatus(), dto.cpf());

        return solicitacao;
    }


    @Transactional
    public SolicitacaoSagaDTO aprovar(String cpf) {
        if(!CpfUtils.validar(cpf)){
            log.error("{} --- CPF {} Inválido", SolicitacaoService.class, cpf);
            throw new CpfInvalidoException();
        }

        SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());

        if(solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            log.error("{} --- A solicitação não está pendente: {} ", SolicitacaoService.class, solicitacao.getStatus());
            throw new SolicitacaoNaoPendenteException();
        }

        log.info("{} --- Solicitação antes da aprovação: {}", SolicitacaoService.class, solicitacao.getStatus());
        solicitacao.aprovar();
        log.info("{} --- Solicitação depois da aprovação: {}", SolicitacaoService.class, solicitacao.getStatus());

        log.info("{} --- Salvando solicitação para o CPF {}", SolicitacaoService.class, solicitacao.getCpf());
        solicitacaoRepository.save(solicitacao);

        return new SolicitacaoSagaDTO(
                solicitacao.getNome(),
                solicitacao.getEmail(),
                solicitacao.getCpf(),
                solicitacao.getTelefone(),
                solicitacao.getSalario().toString(), // tomar cuidado com excep
                solicitacao.getEndereco()
        );
    }

    @Transactional
    public SolicitacaoEntity rejeitar(String cpf, RecusarSolicitacaoRequest dto) {
        log.info("{} --- Iniciando rejeicao da solicitacao para o CPF {}", SolicitacaoService.class, cpf);
        if(!CpfUtils.validar(cpf)) {
            log.error("CPF {} invalido", cpf);
            throw new CpfInvalidoException();
        }

        SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());
        log.info("{} --- Status da solicitacao: {}", SolicitacaoService.class, solicitacao.getStatus());

        if(solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            log.error("Solicitacao nao pendente: {}", SolicitacaoService.class, solicitacao.getStatus());
            throw new SolicitacaoNaoPendenteException();
        }

        solicitacao.rejeitar(dto.motivo());
        log.info("{} --- Rejeitando solicitacao pelo motivo: {}", SolicitacaoService.class, dto.motivo());
        solicitacaoRepository.save(solicitacao);

        log.info("{} --- Enviando e-mail de rejeicao para {}", SolicitacaoService.class, solicitacao.getEmail());
        solicitacaoProducer.enviarEmailRejeicao(solicitacao.getEmail(), dto.motivo());

        return solicitacao;
    }


    public ListarSolicitacoesReturn listarSolicitacoes(StatusSolicitacao status, int page) {
        log.info("{} --- Iniciando busca das solicitações", SolicitacaoService.class);
        PageRequest pageable = PageRequest.of(page, 50);
        List<SolicitacaoEntity> lista;

        if (status != null) {
            log.info("{} --- Busca de solicitação com filtro de status {}", SolicitacaoService.class, status);
            lista = solicitacaoRepository.findByStatus(status, pageable).getContent();
        } else {
            log.info("{} --- Busca de solicitação sem filtro de status.", SolicitacaoService.class);
            lista = solicitacaoRepository.findAll(pageable).getContent();
        }

        List<SolicitacaoHateoasResponse> listaHateoas = lista.stream()
            .map(solicitacao -> {
                String baseApi = "http://localhost:8080/solicitacoes/";
                Map<String, Map<String, String>> links = new java.util.HashMap<>();
                
                links.put("self", Map.of("href", baseApi + solicitacao.getCpf()));

                if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {
                    links.put("aprovacao", Map.of("href", baseApi + solicitacao.getCpf() + "/aprovacao"));
                    links.put("rejeicao", Map.of("href", baseApi + solicitacao.getCpf() + "/rejeicao"));
                }

                return new SolicitacaoHateoasResponse(
                    solicitacao.getCpf(),
                    solicitacao.getNome(),
                    solicitacao.getEmail(),
                    solicitacao.getTelefone(),
                    solicitacao.getSalario().toString(),
                    solicitacao.getEndereco(),
                    solicitacao.getStatus().toString(),
                    solicitacao.getMotivo(),
                    solicitacao.getDataProcessamento() != null ? solicitacao.getDataProcessamento().toString() : null,
                    links
                );
            }).toList();
        
        return new ListarSolicitacoesReturn(listaHateoas);
    }

    public SolicitacaoHateoasResponse getSolicitacaoByCpf(String cpf){
        log.info("{} --- Iniciando busca de solicitação para o CPF {}", SolicitacaoService.class, cpf);

        SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());
        
        String baseApi = "http://localhost:8080/solicitacoes/";
        Map<String, Map<String, String>> links = new java.util.HashMap<>();
        
        links.put("self", Map.of("href", baseApi + solicitacao.getCpf()));

        if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {
            links.put("aprovacao", Map.of("href", baseApi + solicitacao.getCpf() + "/aprovacao"));
            links.put("rejeicao", Map.of("href", baseApi + solicitacao.getCpf() + "/rejeicao"));
        }

        return new SolicitacaoHateoasResponse(
            solicitacao.getCpf(),
            solicitacao.getNome(),
            solicitacao.getEmail(),
            solicitacao.getTelefone(),
            solicitacao.getSalario().toString(),
            solicitacao.getEndereco(),
            solicitacao.getStatus().toString(),
            solicitacao.getMotivo(),
            solicitacao.getDataProcessamento() != null ? solicitacao.getDataProcessamento().toString() : null,
            links
        );
    }

    private void validarPorCPF(String cpf){
        if(solicitacaoRepository.findByCpf(cpf).isPresent()){
            throw new CpfUtilizadoException();
        }
    }
    
    private void validarEmailSolicitado(String email){
        if(solicitacaoRepository.findByEmail(email).isPresent()){
            throw new EmailSolicitadoException();
        }
    }

    private void validarEmailCadastrado(String email){
        //chama AUTH
    }


    //-------------------------Erros SAGA----------------------------------------------------

    @Transactional
    public void retornarSolicitacaoParaPendente(String cpf){
        // SolicitacaoEntity solicitacao = getSolicitacaoByCpf(cpf);
        SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());
        
        solicitacao.retornarParaPendente();
        solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public void marcarSolicitacaoComoNaoAprovada(String cpf, String motivo){
        // SolicitacaoEntity solicitacao = getSolicitacaoByCpf(cpf);
        SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());
        
        solicitacao.rejeitar(motivo);
        solicitacaoRepository.save(solicitacao);
    }
}
