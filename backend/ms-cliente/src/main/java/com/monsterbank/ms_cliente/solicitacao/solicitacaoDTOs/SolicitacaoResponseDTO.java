package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;
import com.monsterbank.ms_cliente.solicitacao.StatusSolicitacao;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitacaoResponseDTO extends RepresentationModel<SolicitacaoResponseDTO> {

    private final String cpf;
    private final String nome;
    private final String email;
    private final String telefone;
    private final BigDecimal salario;
    private final Endereco endereco;
    private final StatusSolicitacao status;
    private final String motivo;
    private final LocalDateTime dataSolicitacao;
    private final LocalDateTime dataProcessamento;

    public SolicitacaoResponseDTO(SolicitacaoEntity entity) {
        this.cpf = entity.getCpf();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        this.telefone = entity.getTelefone();
        this.salario = entity.getSalario();
        this.endereco = entity.getEndereco();
        this.status = entity.getStatus();
        this.motivo = entity.getMotivo();
        this.dataSolicitacao = entity.getDataSolicitacao();
        this.dataProcessamento = entity.getDataProcessamento();
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public BigDecimal getSalario() { return salario; }
    public Endereco getEndereco() { return endereco; }
    public StatusSolicitacao getStatus() { return status; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
}
