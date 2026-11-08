package com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs;

import com.monsterbank.ms_cliente.solicitacao.SolicitacaoEntity;
import com.monsterbank.ms_cliente.solicitacao.StatusSolicitacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoResponseDTO(
        String cpf,
        String nome,
        String email,
        String telefone,
        BigDecimal salario,
        Endereco endereco,
        StatusSolicitacao status,
        String motivo,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataProcessamento
) {
    public SolicitacaoResponseDTO(SolicitacaoEntity entity) {
        this(
                entity.getCpf(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getSalario(),
                entity.getEndereco(),
                entity.getStatus(),
                entity.getMotivo(),
                entity.getDataSolicitacao(),
                entity.getDataProcessamento()
        );
    }
}