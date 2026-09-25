package com.monsterbank.ms_cliente.mensageria.consumer;

import com.monsterbank.ms_cliente.cliente.ClienteService;
import com.monsterbank.ms_cliente.exception.*;
import com.monsterbank.ms_cliente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_cliente.mensageria.dto.SagaReply;
import com.monsterbank.ms_cliente.mensageria.enumeration.ClienteReplyQueue;
import com.monsterbank.ms_cliente.mensageria.producer.OrquestradorResponseProducer;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ClienteCommandConsumer {

    private final SolicitacaoService solicitacaoService;
    private final ClienteService clienteService;
    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;

    public ClienteCommandConsumer(
            SolicitacaoService solicitacaoService,
            ClienteService clienteService,
            ObjectMapper objectMapper,
            OrquestradorResponseProducer responseProducer
    ) {
        this.solicitacaoService = solicitacaoService;
        this.clienteService = clienteService;
        this.objectMapper = objectMapper;
        this.responseProducer = responseProducer;
    }

    @RabbitListener(queues = "ms.cliente.cmd")
    public void receber(SagaCommand command){
        ClienteReplyQueue replyQueue = ClienteReplyQueue.fromCommandType(command.tipo())
                .orElse(ClienteReplyQueue.COMANDO_DESCONHECIDO);

        try{
            switch (command.tipo()){

                case "cliente.aprovar-solicitacao" -> {
                    String cpf = command.payload().get("cpf").asString();

                    SolicitacaoSagaDTO solicitacao = solicitacaoService.aprovar(cpf);


                    JsonNode payload = objectMapper.valueToTree(solicitacao);

                    enviarRespostaSucesso(command, replyQueue, payload);
                }

                case "cliente.criar" ->{
                    String cpf = command.payload().get("cpf").asString();

                    clienteService.criarCliente(cpf);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, replyQueue, payload);

                }

                case "cliente.compensar-aprovacao" ->{
                    String cpf = command.payload().get("cpf").asString();

                    solicitacaoService.retornarSolicitacaoParaPendente(cpf);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, replyQueue, payload);
                }

                case "cliente.marcar-nao-aprovada" ->{
                    String cpf = command.payload().get("cpf").asString();

                    String motivo = command.payload().get("motivo").asString();

                    solicitacaoService.marcarSolicitacaoComoNaoAprovada(cpf, motivo);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, replyQueue, payload);
                }

                case "cliente.compensar-criacao" ->{
                    String cpf = command.payload().get("cpf").asString();

                    clienteService.removerCliente(cpf);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, replyQueue, payload);
                }

                default -> enviarRespostaErro(command, replyQueue, "COMANDO_DESCONHECIDO");

            }
        } catch(CpfInvalidoException e){
            enviarRespostaErro(
                    command,
                    replyQueue,
                    "CPF_INVALIDO"
            );

        } catch (SolicitacaoNaoPendenteException e){
            enviarRespostaErro(
                    command,
                    replyQueue,
                    "SOLICITACAO_NAO_PENDENTE"
            );
        } catch (ErroCriacaoClienteException e){
            enviarRespostaErro(
                    command,
                    replyQueue,
                    "ERRO_CRIACAO_CLIENTE"
            );
        } catch (SolicitacaoNaoEncontradaException e){
            enviarRespostaErro(
                    command,
                    replyQueue,
                    "SOLICITACAO_NAO_ENCONTRADA"
            );
        } catch (ClienteNaoEncontradoException e){
            enviarRespostaErro(
                    command,
                    replyQueue,
                    "CLIENTE_NAO_ENCONTRADO"
            );
        }


    }

    private void enviarRespostaSucesso(
            SagaCommand command,
            ClienteReplyQueue replyQueue,
            JsonNode payload
    ){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                "SUCESSO",
                null,
                payload

        );

        responseProducer.enviar(replyQueue, reply);
    }

    private void enviarRespostaErro(
            SagaCommand command,
            ClienteReplyQueue replyQueue,
            String erro
    ){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                "FALHA",
                erro,
                objectMapper.createObjectNode()

        );

        responseProducer.enviar(replyQueue, reply);
    }

}
