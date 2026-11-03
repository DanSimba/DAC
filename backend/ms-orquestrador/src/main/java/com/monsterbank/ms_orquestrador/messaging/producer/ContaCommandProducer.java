package com.monsterbank.ms_orquestrador.messaging.producer;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;

@Component 
public class ContaCommandProducer {

    public void enviar(String sagaId, String tipo, JsonNode payload) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'enviar'");
    }
}
