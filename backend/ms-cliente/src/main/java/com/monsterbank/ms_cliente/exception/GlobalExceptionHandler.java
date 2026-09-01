package com.monsterbank.ms_cliente.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CpfUtilizadoException.class)
    public ResponseEntity<ErrorResponse> handleCpfSolicitado(CpfUtilizadoException e){
        ErrorResponse erro = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(EmailSolicitadoException.class)
    public ResponseEntity<ErrorResponse> handleEmailSolicitado(EmailSolicitadoException e){
        ErrorResponse erro = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(SalarioInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleSalarioInvalido(SalarioInvalidoException e){
        ErrorResponse erro = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleCpfInvalido(CpfInvalidoException e){
        ErrorResponse erro = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MotivoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleMotivoInvalido(MotivoInvalidoException e){
        ErrorResponse erro = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(SolicitacaoNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitacaoNaoEncontrada(SolicitacaoNaoEncontradaException e){
        ErrorResponse erro = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }


}
