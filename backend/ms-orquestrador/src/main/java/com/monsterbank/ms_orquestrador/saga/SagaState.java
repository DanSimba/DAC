package com.monsterbank.ms_orquestrador.saga;

import tools.jackson.databind.JsonNode;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SagaState {
    public enum Status { EM_ANDAMENTO, COMPENSANDO, CONCLUIDA, FALHA }

    private final String sagaId;

    @JsonIgnore
    private final String cpf;
    
    @JsonProperty("payload")
    private final JsonNode payloadInicial;
    
    @JsonProperty("timestamp")
    private LocalDateTime atualizadoEm;
    
    private String etapaAtual;
    private Status status;
    private String erro;
    private String tipo;
    private Integer etapaAtual_teste;

    // Comentado para teste
    // public SagaState(String sagaId, String cpf, JsonNode payloadInicial, String etapaAtual) {
    //     this.sagaId = sagaId;
    //     this.cpf = cpf;
    //     this.payloadInicial = payloadInicial;
    //     this.etapaAtual = etapaAtual;
    //     this.status = Status.EM_ANDAMENTO;
    //     this.atualizadoEm = LocalDateTime.now();
    // }

    public SagaState(String sagaId, String tipo, JsonNode payloadInicial, Integer etapaAtual) {
        this.sagaId = sagaId;
        this.tipo = tipo;
        this.cpf = payloadInicial.get("cpf").asString();
        this.payloadInicial = payloadInicial;
        this.etapaAtual_teste = etapaAtual;
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
