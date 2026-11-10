package com.monsterbank.ms_cliente.utils;

import com.monsterbank.ms_cliente.mensageria.enumeration.ClienteReplyQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String CLIENTE_COMMAND_QUEUE = "ms.cliente.cmd";
    public static final String CLIENTE_COMMAND_RETRY_QUEUE = "ms.cliente.cmd.retry";
    public static final String CLIENTE_COMMAND_DLQ = "ms.cliente.cmd.dlq";

    @Bean
    public Queue clienteCmdQueue(){
        return new Queue(CLIENTE_COMMAND_QUEUE, true);
    }

    @Bean
    public Queue clienteCmdDlq(){
        return new Queue(CLIENTE_COMMAND_DLQ, true);
    }

    @Bean
    public Queue clienteCmdRetryQueue(){
        return QueueBuilder.durable(CLIENTE_COMMAND_RETRY_QUEUE)
                .ttl(5000)
                .deadLetterExchange("")
                .deadLetterRoutingKey(CLIENTE_COMMAND_QUEUE)
                .build();
    }

    @Bean
    public Queue aprovarSolicitacaoReplyQueue(){
        return new Queue(ClienteReplyQueue.APROVAR_SOLICITACAO.queueName(), true);
    }

    @Bean
    public Queue criarClienteReplyQueue(){
        return new Queue(ClienteReplyQueue.CRIAR.queueName(), true);
    }

    @Bean
    public Queue compensarAprovacaoReplyQueue(){
        return new Queue(ClienteReplyQueue.COMPENSAR_APROVACAO.queueName(), true);
    }

    @Bean
    public Queue marcarNaoAprovadaReplyQueue(){
        return new Queue(ClienteReplyQueue.MARCAR_NAO_APROVADA.queueName(), true);
    }

    @Bean
    public Queue compensarCriacaoReplyQueue(){
        return new Queue(ClienteReplyQueue.COMPENSAR_CRIACAO.queueName(), true);
    }

    @Bean
    public Queue comandoDesconhecidoReplyQueue(){
        return new Queue(ClienteReplyQueue.COMANDO_DESCONHECIDO.queueName(), true);
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
