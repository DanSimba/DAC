package com.monsterbank.ms_cliente.solicitacao;

import com.monsterbank.ms_cliente.exception.*;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.listarSolicitacoesReturn;
import com.monsterbank.ms_cliente.utils.CpfUtils;

import com.monsterbank.ms_cliente.exception.SalarioInvalidoException;
import com.monsterbank.ms_cliente.exception.CpfUtilizadoException;
import com.monsterbank.ms_cliente.exception.EmailSolicitadoException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoEncontradaException;
import com.monsterbank.ms_cliente.mensageria.producer.SolicitacaoProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

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
    public void registrar(RegistrarSolicitacaoRequest dto) {
        log.info("Iniciando registro de solicitação para o CPF: {}", dto.cpf());

        validarPorCPF(dto.cpf());
        validarEmailSolicitado(dto.email());
        validarEmailCadastrado(dto.email());

        BigDecimal salario;
        
        try {
            salario = new BigDecimal(dto.salario());
            if(salario.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Salario zerado ou negativo para o CPF: {}", dto.cpf());
                throw new SalarioInvalidoException(); 
            }
        } catch (NumberFormatException e) {
            log.error("Erro ao converter salario para BigDecimal: {}, CPF: {}", dto.salario(), dto.cpf());
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

        solicitacaoRepository.save(solicitacao);
        log.info("Solicitacao salva como {} para o CPF: {}", solicitacao.getStatus(), dto.cpf());

        SolicitacaoSagaDTO eventoDTO = new SolicitacaoSagaDTO(
            solicitacao.getNome(),
            solicitacao.getEmail(),
            solicitacao.getCpf(),
            solicitacao.getTelefone(),
            solicitacao.getSalario().toString(),
            solicitacao.getEndereco()
        );

        solicitacaoProducer.enviarParaAnalise(eventoDTO);
        log.info("Solicitacao enviada para analise via RabbitMQ. Status: {}. CPF: {}", solicitacao.getStatus(), dto.cpf());

    }


    public SolicitacaoSagaDTO aprovar(String cpf) {
        if(!CpfUtils.validar(cpf)){
            throw new CpfInvalidoException();
        }

        SolicitacaoEntity solicitacao = getSolicitacaoByCpf(cpf);

        if(solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new SolicitacaoNaoPendenteException();
        }

        solicitacao.aprovar();
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

    public void rejeitar(String cpf, String motivo) {

        if(!CpfUtils.validar(cpf)){
            throw new CpfInvalidoException();
        }

        SolicitacaoEntity solicitacao = getSolicitacaoByCpf(cpf);

        if(solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new SolicitacaoNaoPendenteException();
        }

        solicitacao.rejeitar(motivo);
        solicitacaoRepository.save(solicitacao);
    }


    public List<listarSolicitacoesReturn> listarSolicitacoes(int page){
        PageRequest peageable = PageRequest.of(page, 50);
        List<SolicitacaoEntity> lista = solicitacaoRepository.findAll(peageable).getContent();

        return lista.stream()
                .map(solicitacao -> new listarSolicitacoesReturn(
                        solicitacao.getCpf(),
                        solicitacao.getNome(),
                        solicitacao.getSalario().toString(),
                        solicitacao.getStatus().toString(),
                        solicitacao.getMotivo() != null ? solicitacao.getMotivo() : null,
                        solicitacao.getDataProcessamento() != null? solicitacao.getDataProcessamento().toString() : null
                )).toList();

    }

    public SolicitacaoEntity getSolicitacaoByCpf(String cpf){
        return solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());
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

    public void retornarSolicitacaoParaPendente(String cpf){

        SolicitacaoEntity solicitacao = getSolicitacaoByCpf(cpf);

        solicitacao.retornarParaPendente();

        solicitacaoRepository.save(solicitacao);

    }


    public void marcarSolicitacaoComoNaoAprovada(String cpf, String motivo){
        SolicitacaoEntity solicitacao = getSolicitacaoByCpf(cpf);
        solicitacao.rejeitar(motivo);
        solicitacaoRepository.save(solicitacao);

    }


}
