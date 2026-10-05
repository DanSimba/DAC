package com.monsterbank.ms_orquestrador.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String CLIENTE_COMMAND_QUEUE = "ms.cliente.cmd";
    public static final String CLIENTE_APROVAR_SOLICITACAO_REPLY_QUEUE = "ms.cliente.aprovar-solicitacao.reply";
    public static final String CLIENTE_CRIAR_REPLY_QUEUE = "ms.cliente.criar.reply";
    public static final String CLIENTE_COMPENSAR_APROVACAO_REPLY_QUEUE = "ms.cliente.compensar-aprovacao.reply";
    public static final String CLIENTE_MARCAR_NAO_APROVADA_REPLY_QUEUE = "ms.cliente.marcar-nao-aprovada.reply";
    public static final String CLIENTE_COMPENSAR_CRIACAO_REPLY_QUEUE = "ms.cliente.compensar-criacao.reply";
    public static final String CLIENTE_COMANDO_DESCONHECIDO_REPLY_QUEUE = "ms.cliente.comando-desconhecido.reply";

    @Bean
    public Queue clienteCmdQueue(){
        return new Queue(CLIENTE_COMMAND_QUEUE, true);
    }

    @Bean
    public Queue clienteAprovarSolicitacaoReplyQueue() {
        return new Queue(CLIENTE_APROVAR_SOLICITACAO_REPLY_QUEUE, true);
    }

    @Bean
    public Queue clienteCriarReplyQueue() {
        return new Queue(CLIENTE_CRIAR_REPLY_QUEUE, true);
    }

    @Bean
    public Queue clienteCompensarAprovacaoReplyQueue() {
        return new Queue(CLIENTE_COMPENSAR_APROVACAO_REPLY_QUEUE, true);
    }

    @Bean
    public Queue clienteMarcarNaoAprovadaReplyQueue() {
        return new Queue(CLIENTE_MARCAR_NAO_APROVADA_REPLY_QUEUE, true);
    }

    @Bean
    public Queue clienteCompensarCriacaoReplyQueue() {
        return new Queue(CLIENTE_COMPENSAR_CRIACAO_REPLY_QUEUE, true);
    }

    @Bean
    public Queue clienteComandoDesconhecidoReplyQueue() {
        return new Queue(CLIENTE_COMANDO_DESCONHECIDO_REPLY_QUEUE, true);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}
