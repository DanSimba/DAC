package com.monsterbank.ms_cliente.solicitacao;

import com.monsterbank.ms_cliente.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.listarSolicitacoesReturn;
import com.monsterbank.ms_cliente.utils.CpfUtils;

import com.monsterbank.ms_cliente.exception.SalarioInvalidoException;
import com.monsterbank.ms_cliente.exception.CpfUtilizadoException;
import com.monsterbank.ms_cliente.exception.EmailSolicitadoException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoEncontradaException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoPendenteException;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    // @Autowired
    // private SolicitacaoRepository solicitacaoRepository;

    private final SolicitacaoRepository solicitacaoRepository;

    public SolicitacaoService (SolicitacaoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public void registrar(RegistrarSolicitacaoRequest dto){
        validarPorCPF(dto.cpf());
        validarEmailSolicitado(dto.email());
        validarEmailCadastrado(dto.email());

        BigDecimal salario;
        
        try {
            salario = new BigDecimal(dto.salario());
            if(salario.compareTo(BigDecimal.ZERO) <= 0) { throw new SalarioInvalidoException(); }
        } catch (NumberFormatException e) {
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

    public void aprovarSolicitacao(String cpf){

        if(!CpfUtils.validar(cpf)){
            throw new CpfInvalidoException();
        }

        Optional<SolicitacaoEntity> solicitacao = solicitacaoRepository.findByCpf(cpf);

        if(solicitacao.isPresent()){
            solicitacao.get().aprovar();
            solicitacaoRepository.save(solicitacao.get());
        }else{
            throw new SolicitacaoNaoEncontradaException();
        }

    }

    private void rejeitarSolicitacao(String cpf, String motivo){

        if(!CpfUtils.validar(cpf)) {
            throw new CpfInvalidoException();
        }

        if (motivo.isEmpty()){
            throw new MotivoInvalidoException();
        }

        Optional<SolicitacaoEntity> solicitacao = solicitacaoRepository.findByCpf(cpf);

        if (solicitacao.isPresent()){

            solicitacao.get().rejeitar(motivo);

            solicitacaoRepository.save(solicitacao.get());

        }else{
            throw new SolicitacaoNaoEncontradaException();
        }

    }


    public void retornarSolicitacaoParaPendente(String cpf){

    }


    public void marcarSolicitacaoComoNaoAprovada(String cpf, String motivo){


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

    public void aprovar(String cpf) {
       SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());

       if(solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
        throw new SolicitacaoNaoPendenteException();
       }

       solicitacao.aprovar();
       solicitacaoRepository.save(solicitacao);
    }

    public void rejeitar(String cpf, String motivo) {
       SolicitacaoEntity solicitacao = solicitacaoRepository.findByCpf(cpf).orElseThrow(() -> new SolicitacaoNaoEncontradaException());

       if(solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
        throw new SolicitacaoNaoPendenteException();
       }

       solicitacao.rejeitar(motivo);
       solicitacaoRepository.save(solicitacao);
    }

}
