package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import org.springframework.hateoas.RepresentationModel;

public class SolicitacaoHateoasResponse extends RepresentationModel<SolicitacaoHateoasResponse> {

    private final String cpf;
    private final String nome;
    private final String email;
    private final String telefone;
    private final String salario;
    private final Endereco endereco;
    private final String status;
    private final String motivo;
    private final String dataHoraProcessamento;

    public SolicitacaoHateoasResponse(
            String cpf,
            String nome,
            String email,
            String telefone,
            String salario,
            Endereco endereco,
            String status,
            String motivo,
            String dataHoraProcessamento
    ) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.salario = salario;
        this.endereco = endereco;
        this.status = status;
        this.motivo = motivo;
        this.dataHoraProcessamento = dataHoraProcessamento;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSalario() { return salario; }
    public Endereco getEndereco() { return endereco; }
    public String getStatus() { return status; }
    public String getMotivo() { return motivo; }
    public String getDataHoraProcessamento() { return dataHoraProcessamento; }
}
