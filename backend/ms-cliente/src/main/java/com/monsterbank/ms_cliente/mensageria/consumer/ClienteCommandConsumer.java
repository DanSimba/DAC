package com.monsterbank.ms_cliente.mensageria.consumer;

import com.monsterbank.ms_cliente.cliente.ClienteService;
import com.monsterbank.ms_cliente.exception.ClienteNaoEncontradoException;
import com.monsterbank.ms_cliente.exception.CpfInvalidoException;
import com.monsterbank.ms_cliente.exception.ErroCriacaoClienteException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoEncontradaException;
import com.monsterbank.ms_cliente.exception.SolicitacaoNaoPendenteException;
import com.monsterbank.ms_cliente.mensageria.dto.SagaCommand;
import com.monsterbank.ms_cliente.mensageria.dto.SagaReply;
import com.monsterbank.ms_cliente.mensageria.enumeration.ClienteReplyQueue;
import com.monsterbank.ms_cliente.mensageria.idempotencia.ComandoProcessadoEntity;
import com.monsterbank.ms_cliente.mensageria.idempotencia.ComandoProcessadoRepository;
import com.monsterbank.ms_cliente.mensageria.producer.OrquestradorResponseProducer;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoSagaDTO;
import com.monsterbank.ms_cliente.utils.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
public class ClienteCommandConsumer {

    private final SolicitacaoService solicitacaoService;
    private final ClienteService clienteService;
    private final ObjectMapper objectMapper;
    private final OrquestradorResponseProducer responseProducer;
    private final ComandoProcessadoRepository comandoProcessadoRepository;
    private final RabbitTemplate rabbitTemplate;

    public ClienteCommandConsumer(
            SolicitacaoService solicitacaoService,
            ClienteService clienteService,
            ObjectMapper objectMapper,
            OrquestradorResponseProducer responseProducer,
            ComandoProcessadoRepository comandoProcessadoRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.solicitacaoService = solicitacaoService;
        this.clienteService = clienteService;
        this.objectMapper = objectMapper;
        this.responseProducer = responseProducer;
        this.comandoProcessadoRepository = comandoProcessadoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_COMMAND_QUEUE)
    public void receber(
            SagaCommand command,
            @Header(name = "x-retry-count", defaultValue = "0") int tentativa
    ) {
        try {
            validar(command);
            String chave = chave(command);
            ClienteReplyQueue replyQueue = ClienteReplyQueue.fromCommandType(command.tipo())
                    .orElse(ClienteReplyQueue.COMANDO_DESCONHECIDO);

            comandoProcessadoRepository.findById(chave).ifPresentOrElse(
                    processado -> responseProducer.enviar(replyQueue, toReply(processado)),
                    () -> processarNovo(command, chave, replyQueue)
            );
        } catch (RuntimeException e) {
            tratarFalhaTecnica(command, tentativa);
        }
    }

    private void processarNovo(SagaCommand command, String chave, ClienteReplyQueue replyQueue) {
        try {
            JsonNode payload = executar(command);
            responder(command, chave, replyQueue, "SUCESSO", null, payload);
        } catch (CpfInvalidoException e) {
            responder(command, chave, replyQueue, "FALHA", "CPF_INVALIDO", objectMapper.createObjectNode());
        } catch (SolicitacaoNaoPendenteException e) {
            responder(command, chave, replyQueue, "FALHA", "SOLICITACAO_NAO_PENDENTE", objectMapper.createObjectNode());
        } catch (ErroCriacaoClienteException e) {
            responder(command, chave, replyQueue, "FALHA", "ERRO_CRIACAO_CLIENTE", objectMapper.createObjectNode());
        } catch (SolicitacaoNaoEncontradaException e) {
            responder(command, chave, replyQueue, "FALHA", "SOLICITACAO_NAO_ENCONTRADA", objectMapper.createObjectNode());
        } catch (ClienteNaoEncontradoException e) {
            responder(command, chave, replyQueue, "FALHA", "CLIENTE_NAO_ENCONTRADO", objectMapper.createObjectNode());
        } catch (ComandoDesconhecidoException e) {
            responder(command, chave, replyQueue, "FALHA", "COMANDO_DESCONHECIDO", objectMapper.createObjectNode());
        }
    }

    private JsonNode executar(SagaCommand command) {
        String cpf = command.payload().path("cpf").asText();

        return switch (command.tipo()) {
            case "cliente.aprovar-solicitacao" -> {
                SolicitacaoSagaDTO solicitacao = solicitacaoService.aprovar(cpf);
                yield objectMapper.valueToTree(solicitacao);
            }
            case "cliente.criar" -> {
                clienteService.criarCliente(cpf);
                yield objectMapper.createObjectNode();
            }
            case "cliente.compensar-aprovacao" -> {
                solicitacaoService.retornarSolicitacaoParaPendente(cpf);
                yield objectMapper.createObjectNode();
            }
            case "cliente.marcar-nao-aprovada" -> {
                String motivo = command.payload().path("motivo").asText();
                solicitacaoService.marcarSolicitacaoComoNaoAprovada(cpf, motivo);
                yield objectMapper.createObjectNode();
            }
            case "cliente.compensar-criacao" -> {
                clienteService.removerCliente(cpf);
                yield objectMapper.createObjectNode();
            }
            default -> throw new ComandoDesconhecidoException();
        };
    }

    private void responder(
            SagaCommand command,
            String chave,
            ClienteReplyQueue replyQueue,
            String status,
            String erro,
            JsonNode payload
    ) {
        SagaReply reply = new SagaReply(
                command.sagaId(),
                LocalDateTime.now().toString(),
                status,
                erro,
                payload
        );

        ComandoProcessadoEntity processado = new ComandoProcessadoEntity(
                chave,
                reply.sagaId(),
                command.tipo(),
                reply.timestamp(),
                reply.status(),
                reply.erro(),
                reply.payload().toString()
        );

        try {
            comandoProcessadoRepository.saveAndFlush(processado);
            responseProducer.enviar(replyQueue, reply);
        } catch (DataIntegrityViolationException e) {
            ComandoProcessadoEntity existente = comandoProcessadoRepository.findById(chave)
                    .orElseThrow(() -> e);
            responseProducer.enviar(replyQueue, toReply(existente));
        }
    }

    private SagaReply toReply(ComandoProcessadoEntity processado) {
        try {
            return new SagaReply(
                    processado.getSagaId(),
                    processado.getTimestamp(),
                    processado.getStatus(),
                    processado.getErro(),
                    objectMapper.readTree(processado.getPayload())
            );
        } catch (JacksonException e) {
            throw new IllegalStateException("Resposta idempotente inválida", e);
        }
    }

    private void validar(SagaCommand command) {
        if (command == null
                || command.sagaId() == null
                || command.sagaId().isBlank()
                || command.tipo() == null
                || command.tipo().isBlank()
                || command.payload() == null) {
            throw new IllegalArgumentException("Comando de saga inválido");
        }
    }

    private String chave(SagaCommand command) {
        return command.sagaId() + ":" + command.tipo();
    }

    private void tratarFalhaTecnica(SagaCommand command, int tentativa) {
        if (tentativa < 2) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.CLIENTE_COMMAND_RETRY_QUEUE,
                    command,
                    message -> {
                        message.getMessageProperties().setHeader("x-retry-count", tentativa + 1);
                        return message;
                    }
            );
            return;
        }

        rabbitTemplate.convertAndSend(RabbitConfig.CLIENTE_COMMAND_DLQ, command);
    }

    private static final class ComandoDesconhecidoException extends RuntimeException {
    }
}
