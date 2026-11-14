package com.monsterbank.ms_account.mensageria.dto;

import com.monsterbank.ms_account.events.enums.EventType;
import java.time.LocalDateTime;
import java.util.Map;

public class EventMessageDTO {

    private Long id;
    private String objetoId;
    private EventType tipo;
    private Map<String, Object> payload; //TROCA O PAYLOAD DE JSONNODE PRA MAPA PQ AS VERSÕES DO CONVERSOR (JACKSON) TAVAM DANDO ERRO E CONVERTENDO ERRADO
    private Long versao;
    private LocalDateTime datetime;

    public EventMessageDTO() {}

    public EventMessageDTO(
        Long id,
        String objetoId,
        EventType tipo,
        Map<String, Object> payload,
        Long versao,
        LocalDateTime datetime
    ) {
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

    public Map<String, Object> getPayload() { 
        return payload; 
    }

    public void setPayload(Map<String, Object> payload) { 
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
