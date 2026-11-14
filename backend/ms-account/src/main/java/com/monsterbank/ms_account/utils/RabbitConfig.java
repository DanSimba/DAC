package com.monsterbank.ms_account.utils;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import com.monsterbank.ms_account.mensageria.enumeration.AccountReplyQueue;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public Queue accountCmdQueue(){
        return new Queue("ms.conta.cmd", true);
    }

    @Bean
    public Queue aprovarSolicitacaoReplyQueue(){
        return new Queue(AccountReplyQueue.APROVAR_SOLICITACAO.queueName(), true);
    }

    @Bean
    public Queue criarClienteReplyQueue(){
        return new Queue(AccountReplyQueue.CRIAR.queueName(), true);
    }

    @Bean
    public Queue compensarAprovacaoReplyQueue(){
        return new Queue(AccountReplyQueue.COMPENSAR_APROVACAO.queueName(), true);
    }

    @Bean
    public Queue marcarNaoAprovadaReplyQueue(){
        return new Queue(AccountReplyQueue.MARCAR_NAO_APROVADA.queueName(), true);
    }

    @Bean
    public Queue compensarCriacaoReplyQueue(){
        return new Queue(AccountReplyQueue.COMPENSAR_CRIACAO.queueName(), true);
    }

    @Bean
    public Queue comandoDesconhecidoReplyQueue(){
        return new Queue(AccountReplyQueue.COMANDO_DESCONHECIDO.queueName(), true);
    }

    //fila que vai pro banco read
    @Bean
    public Queue accountEventsQueue() {
        return new Queue("ms.conta.eventos", true);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
