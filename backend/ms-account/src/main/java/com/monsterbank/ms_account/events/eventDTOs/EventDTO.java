package com.monsterbank.ms_account.events.eventDTOs;

import com.fasterxml.jackson.databind.JsonNode;

import com.monsterbank.ms_account.events.enums.EventType;

import java.time.LocalDateTime;

public class EventDTO {

    private Long id;
    private String objetoId;
    private EventType tipo;
    private JsonNode payload;
    private Long versao;
    private LocalDateTime datetime;

    public EventDTO() {}

    public EventDTO(
            Long id,
            String objetoId,
            EventType tipo,
            JsonNode payload,
            Long versao,
            LocalDateTime datetime
    ){
        this.id = id;
        this.objetoId = objetoId;
        this.tipo = tipo;
        this.payload = payload;
        this.versao = versao;
        this.datetime = datetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjetoId() {
        return objetoId;
    }

    public void setObjetoId(String objetoId) {
        this.objetoId = objetoId;
    }

    public EventType getTipo() {
        return tipo;
    }

    public void setTipo(EventType tipo) {
        this.tipo = tipo;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
