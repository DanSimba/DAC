package com.monsterbank.ms_cliente.mensageria;

import org.junit.jupiter.api.Test;

import com.monsterbank.ms_cliente.mensageria.enumeration.ClienteReplyQueue;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteReplyQueueTest {

    @Test
    void deveMapearCadaOperacaoParaUmaFilaDeRespostaDiferente() {
        assertThat(ClienteReplyQueue.fromCommandType("cliente.aprovar-solicitacao"))
                .contains(ClienteReplyQueue.APROVAR_SOLICITACAO);
        assertThat(ClienteReplyQueue.fromCommandType("cliente.criar"))
                .contains(ClienteReplyQueue.CRIAR);
        assertThat(ClienteReplyQueue.fromCommandType("cliente.compensar-aprovacao"))
                .contains(ClienteReplyQueue.COMPENSAR_APROVACAO);
        assertThat(ClienteReplyQueue.fromCommandType("cliente.marcar-nao-aprovada"))
                .contains(ClienteReplyQueue.MARCAR_NAO_APROVADA);
        assertThat(ClienteReplyQueue.fromCommandType("cliente.compensar-criacao"))
                .contains(ClienteReplyQueue.COMPENSAR_CRIACAO);
    }

    @Test
    void naoDeveMapearComandoDesconhecidoComoOperacaoValida() {
        assertThat(ClienteReplyQueue.fromCommandType("cliente.inexistente")).isEmpty();
    }
}
