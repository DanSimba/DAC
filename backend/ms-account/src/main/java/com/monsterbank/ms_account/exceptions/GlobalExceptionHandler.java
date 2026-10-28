package com.monsterbank.ms_account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        //EXCEÇÃO DE SALDO
        @ExceptionHandler(SaldoInsuficienteException.class)
        public ResponseEntity<ErrorResponse> handleSaldoInsuficiente(SaldoInsuficienteException e) {

                ErrorResponse error = new ErrorResponse(
                        403, //403: forbidden, nao tem dinheiro bro
                        "SALDO_INSUFICIENTE",
                        e.getMessage()
                );

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(error);
        }

        //EXCEÇÃO DE ERRO AO CRIAR CONTA
        @ExceptionHandler(ErroCriacaoAccountException.class)
        public ResponseEntity<ErrorResponse> handleErroCriacaoAccount(ErroCriacaoAccountException e) {

                ErrorResponse error = new ErrorResponse(
                        400, //bad request, as vzs vai ser 500 mas provavelmente vai ser culpa do usuário a maior parte das vzs
                        "ERRO_AO_CRIAR_CONTA",
                        e.getMessage()
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(error);
        }

         //EXCEÇÃO DE ERRO CONTA NÃO ENCONTRADA
        @ExceptionHandler(ErroContaNaoEncontradaException.class)
        public ResponseEntity<ErrorResponse> handleErroContaNaoEncontrada(ErroContaNaoEncontradaException e) {
                ErrorResponse error = new ErrorResponse(
                        400, //bad request: mandou um cona que nao existe
                        "ERRO_AO_BUSCAR_CONTA",
                        e.getMessage()
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(error);
        }

        //TRATAMENTO GENERICO
        /*@ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(
                Exception e) {
                        
                 e.printStackTrace();

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse(
                        500, //famoso "tem que ver isso ae"
                        "ERRO_INTERNO",
                        "Erro genérico!!! :("
                        ));
        }*/

        @ExceptionHandler(Exception.class)
        public Exception handleGeneric( //enquanto eu estiver debuggando eu vou fzr isso aq msm
                Exception e) {
                        return e;
        }
}
