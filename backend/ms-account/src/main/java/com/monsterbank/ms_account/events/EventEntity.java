package com.monsterbank.ms_account.events;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.monsterbank.ms_account.events.EventType;

import java.time.LocalDateTime;

@Entity
@Table( //DECLARA A TABLE DNV PRA CRIAR A CHAVE COMPOSTA
    name = "eventos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_evento_objeto_versao",
            columnNames = {"objeto_id", "versao"}
        )
    }
)
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "objeto_id", nullable = false)
    private String objetoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType tipo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode payload;

    @Column(nullable = false)
    private Long versao;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected EventEntity(){}

    public EventEntity(
            String objetoId,
            EventType tipo,
            JsonNode payload,
            Long versao
    ){
        this.objetoId = objetoId;
        this.tipo = tipo;
        this.payload = payload;
        this.versao = versao;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getObjetoId() {
        return objetoId;
    }

    public EventType getTipo() {
        return tipo;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public Long getVersao() {
        return versao;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setObjetoId(String objetoId) {
        this.objetoId = objetoId;
    }

    public void setTipo(EventType tipo) {
        this.tipo = tipo;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }
}
