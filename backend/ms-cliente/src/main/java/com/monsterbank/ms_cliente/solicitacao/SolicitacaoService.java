package com.monsterbank.ms_cliente.solicitacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.exception.SalarioInvalidoException;
import com.monsterbank.ms_cliente.exception.CpfUtilizadoException;
import com.monsterbank.ms_cliente.exception.EmailSolicitadoException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoEncontradaException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoPendenteException;

import java.math.BigDecimal;

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
