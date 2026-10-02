package com.monsterbank.ms_account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //EXCEÇÃO DE SALDO
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficiente(
            SaldoInsuficienteException e) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), //400
                "SALDO_INSUFICIENTE",
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    //EXCEÇÃO DE ERRO AO CRIAR CONTA
    @ExceptionHandler(ErroCriacaoAccountException.class)
    public ResponseEntity<ErrorResponse> handleErroCriacaoAccount(
            SaldoInsuficienteException e) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), //400
                "ERRO_AO_CRIAR_CONTA",
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    //TRATAMENTO GENERICO
     @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                    500, //famoso "tem que ver isso ae"
                    "ERRO_INTERNO",
                    "Erro genérico!!! :("
                ));
    }
}
