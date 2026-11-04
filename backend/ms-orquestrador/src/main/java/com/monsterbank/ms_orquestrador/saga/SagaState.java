package com.monsterbank.ms_orquestrador.saga;

import java.time.LocalDateTime;

import tools.jackson.databind.JsonNode;

public class SagaState {
    public enum Status { EM_ANDAMENTO, COMPENSANDO, CONCLUIDA, FALHA }

    private String sagaId;
    private String tipo;
    private Integer etapaAtual;
    private String operacaoAtual;
    private Status status;
    private JsonNode payload;
    private String timestamp;
    private String erro;

    public SagaState() {
        // Construtor exigido pelo Jackson ao recuperar o estado do Redis.
    }

    public SagaState(
            String sagaId,
            String tipo,
            JsonNode payload,
            String operacaoAtual,
            Integer etapaAtual
    ) {
        this.sagaId = sagaId;
        this.tipo = tipo;
        this.payload = payload;
        this.operacaoAtual = operacaoAtual;
        this.etapaAtual = etapaAtual;
        this.status = Status.EM_ANDAMENTO;
        atualizarTimestamp();
    }

    public void avancarPara(String operacao, int etapa) {
        this.operacaoAtual = operacao;
        this.etapaAtual = etapa;
        this.status = Status.EM_ANDAMENTO;
        atualizarTimestamp();
    }

    public void compensar(String operacao, int etapa, String causa) {
        this.operacaoAtual = operacao;
        this.etapaAtual = etapa;
        this.status = Status.COMPENSANDO;
        this.erro = causa;
        atualizarTimestamp();
    }

    public void concluir() {
        this.status = Status.CONCLUIDA;
        atualizarTimestamp();
    }

    public void falhar(String causa) {
        this.status = Status.FALHA;
        this.erro = causa;
        atualizarTimestamp();
    }

    public boolean finalizada() {
        return status == Status.CONCLUIDA || status == Status.FALHA;
    }

    private void atualizarTimestamp() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public String cpf() {
        JsonNode cpf = payload == null ? null : payload.get("cpf");
        return cpf == null ? null : cpf.asString();
    }

    public String sagaId() { return sagaId; }
    public String operacaoAtual() { return operacaoAtual; }
    public String erro() { return erro; }

    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getEtapaAtual() { return etapaAtual; }
    public void setEtapaAtual(Integer etapaAtual) { this.etapaAtual = etapaAtual; }
    public String getOperacaoAtual() { return operacaoAtual; }
    public void setOperacaoAtual(String operacaoAtual) { this.operacaoAtual = operacaoAtual; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public JsonNode getPayload() { return payload; }
    public void setPayload(JsonNode payload) { this.payload = payload; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }
}
