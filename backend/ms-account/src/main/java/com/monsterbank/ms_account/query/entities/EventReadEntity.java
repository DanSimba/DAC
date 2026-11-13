package com.monsterbank.ms_account.query.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.monsterbank.ms_account.events.enums.EventType;

import java.time.LocalDateTime;

//JUNTA NUMBER DA CONTA (OBJ_ID) COM VERSÃO PARA CHAVE COMPOSTA
@Entity
@Table(
    name = "eventos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_evento_objeto_versao",
            columnNames = {"objeto_id", "versao"}
        )
    }
)
public class EventReadEntity {

    //ID DO BANCO NÃO É COMPARTILHADO
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
    private LocalDateTime datetime;

    public EventReadEntity() {}

    //GETTERS----------FOVHBIEFVHCBIPEHB 

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

    public LocalDateTime getDatetime() {
        return datetime;
    }

    //SETTERS--------MVNJAHG JGH VOUGV 

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

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
