package com.monsterbank.ms_cliente.mensageria.consumer;

import com.monsterbank.ms_cliente.cliente.ClienteService;
import com.monsterbank.ms_cliente.exception.*;
import com.monsterbank.ms_cliente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_cliente.mensageria.dto.SagaReply;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.monsterbank.ms_cliente.mensageria.producer.OrquestradorResponseProducer;

import java.time.LocalDateTime;

@Component
public class ClienteCommandConsumer {

    //private final RabbitTemplate rabbitTemplate;
    private final SolicitacaoService solicitacaoService;
    private final ClienteService clienteService;
    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;

    public ClienteCommandConsumer(
            //RabbitTemplate rabbitTemplate,
            SolicitacaoService solicitacaoService,
            ClienteService clienteService,
            ObjectMapper objectMapper,
            OrquestradorResponseProducer responseProducer
    ) {
        //this.rabbitTemplate = rabbitTemplate;
        this.solicitacaoService = solicitacaoService;
        this.clienteService = clienteService;
        this.objectMapper = objectMapper;
        this.responseProducer = responseProducer;
    }

    @RabbitListener(queues = "ms.cliente.cmd")
    public void receber(SagaCommand command){
        try{
            switch (command.tipo()){

                case "cliente.aprovar-solicitacao" -> {
                    String cpf = command.payload().get("cpf").asString();

                    SolicitacaoSagaDTO solicitacao = solicitacaoService.aprovar(cpf);


                    JsonNode payload = objectMapper.valueToTree(solicitacao);

                    enviarRespostaSucesso(command, payload);
                }

                case "cliente.criar" ->{
                    String cpf = command.payload().get("cpf").asString();

                    clienteService.criarCliente(cpf);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, payload);

                }

                case "cliente.compensar-aprovacao" ->{
                    String cpf = command.payload().get("cpf").asString();

                    solicitacaoService.retornarSolicitacaoParaPendente(cpf);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, payload);
                }

                case "cliente.marcar-nao-aprovada" ->{
                    String cpf = command.payload().get("cpf").asString();

                    String motivo = command.payload().get("motivo").asString();

                    solicitacaoService.marcarSolicitacaoComoNaoAprovada(cpf, motivo);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, payload);
                }

                case "cliente.compensar-criacao" ->{
                    String cpf = command.payload().get("cpf").asString();

                    clienteService.removerCliente(cpf);


                    JsonNode payload = objectMapper.createObjectNode();

                    enviarRespostaSucesso(command, payload);
                }

                default -> enviarRespostaErro(command, "COMANDO_DESCONHECIDO");

            }
        } catch(CpfInvalidoException e){
        enviarRespostaErro(
                command,
                "CPF_INVALIDO"
        );

        } catch (SolicitacaoNaoPendenteException e){
            enviarRespostaErro(
                    command,
                    "SOLICITACAO_NAO_PENDENTE"
            );
        } catch (ErroCriacaoClienteException e){
            enviarRespostaErro(
                    command,
                    "ERRO_CRIACAO_CLIENTE"
            );
        } catch (SolicitacaoNaoEncontradaException e){
            enviarRespostaErro(
                    command,
                    "SOLICITACAO_NAO_ENCONTRADA"
            );
        } catch (ClienteNaoEncontradoException e){
            enviarRespostaErro(
                    command,
                    "CLIENTE_NAO_ENCONTRADO"
            );
        }


    }

    private void enviarRespostaSucesso(SagaCommand command, JsonNode payload){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                command.tipo(),
                LocalDateTime.now().toString(),
                "SUCESSO",
                null,
                payload

        );

        //rabbitTemplate.convertAndSend("orquestrador.reply", reply);
        responseProducer.enviar(reply);
    }

    private void enviarRespostaErro(SagaCommand command, String erro){
        SagaReply reply = new SagaReply(
                command.sagaId(),
                command.tipo(),
                LocalDateTime.now().toString(),
                "FALHA",
                erro,
                objectMapper.createObjectNode()

        );

        //rabbitTemplate.convertAndSend("orquestrador.reply", reply);
        responseProducer.enviar(reply);
    }

}
