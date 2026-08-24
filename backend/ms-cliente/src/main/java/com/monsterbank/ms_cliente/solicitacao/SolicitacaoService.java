package com.monsterbank.ms_cliente.solicitacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.registrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.exception.SalarioInvalidoException;
import com.monsterbank.ms_cliente.exception.CpfUtilizadoException;
import com.monsterbank.ms_cliente.exception.EmailSolicitadoException;

import java.math.BigDecimal;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    public void registrar(registrarSolicitacaoRequest dto){
        validarPorCPF(dto.cpf());
        validarEmaiSolicitado(dto.email());
        validarEmailCadastrado(dto.email());

        BigDecimal salario;

        try {
            salario = new BigDecimal(dto.salario());
        } catch (NumberFormatException e) {
            throw new SalarioInvalidoException();
        }

        SolicitacaoEntity solicitacao = new SolicitacaoEntity(
                dto.cpf(),
                dto.nome(),
                dto.email(),
                dto.telefone(),
                salario,
                dto.logradouro(),
                dto.numero(),
                dto.complemento(),
                dto.cep(),
                dto.cidade(),
                dto.uf()
                );

        solicitacaoRepository.save(solicitacao);

    }


    private void validarPorCPF(String cpf){
        if(solicitacaoRepository.findByCpf(cpf).isPresent()){
            throw new CpfUtilizadoException();
        }
    }

    private void validarEmaiSolicitado(String email){
        if(solicitacaoRepository.findByEmail(email).isPresent()){
            throw new EmailSolicitadoException();
        }
    }

    private void validarEmailCadastrado(String email){
        //chama AUTH
    }

}
