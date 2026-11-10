package com.monsterbank.ms_cliente.mensageria.idempotencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "comandos_processados")
public class ComandoProcessadoEntity {

    @Id
    @Column(length = 300, nullable = false)
    private String chave;

    @Column(nullable = false)
    private String sagaId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false)
    private String status;

    private String erro;

    @Lob
    @Column(nullable = false)
    private String payload;

    protected ComandoProcessadoEntity() {
    }

    public ComandoProcessadoEntity(
            String chave,
            String sagaId,
            String tipo,
            String timestamp,
            String status,
            String erro,
            String payload
    ) {
        this.chave = chave;
        this.sagaId = sagaId;
        this.tipo = tipo;
        this.timestamp = timestamp;
        this.status = status;
        this.erro = erro;
        this.payload = payload;
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getPayload() {
        return payload;
    }
}
