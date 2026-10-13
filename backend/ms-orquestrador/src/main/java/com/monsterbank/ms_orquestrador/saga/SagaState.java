package com.monsterbank.ms_orquestrador.saga;

import tools.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public class SagaState {
    public enum Status { EM_ANDAMENTO, COMPENSANDO, CONCLUIDA, FALHA }

    private final String sagaId;
    private final String cpf;
    private final JsonNode payloadInicial;
    private String etapaAtual;
    private Status status;
    private LocalDateTime atualizadoEm;
    private String erro;

    public SagaState(String sagaId, String cpf, JsonNode payloadInicial, String etapaAtual) {
        this.sagaId = sagaId;
        this.cpf = cpf;
        this.payloadInicial = payloadInicial;
        this.etapaAtual = etapaAtual;
        this.status = Status.EM_ANDAMENTO;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void avancarPara(String etapa) {
        etapaAtual = etapa;
        atualizadoEm = LocalDateTime.now();
    }

    public void compensar(String etapa, String causa) {
        etapaAtual = etapa;
        status = Status.COMPENSANDO;
        erro = causa;
        atualizadoEm = LocalDateTime.now();
    }

    public void concluir() { status = Status.CONCLUIDA; atualizadoEm = LocalDateTime.now(); }
    public void falhar(String causa) { status = Status.FALHA; erro = causa; atualizadoEm = LocalDateTime.now(); }
    public String sagaId() { return sagaId; }
    public String cpf() { return cpf; }
    public JsonNode payloadInicial() { return payloadInicial; }
    public String etapaAtual() { return etapaAtual; }
    public Status status() { return status; }
    public LocalDateTime atualizadoEm() { return atualizadoEm; }
    public String erro() { return erro; }
}
