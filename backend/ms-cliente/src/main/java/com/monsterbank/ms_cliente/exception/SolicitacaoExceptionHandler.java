package com.monsterbank.ms_cliente.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.ErroDTO;

@RestControllerAdvice 
public class SolicitacaoExceptionHandler {

    public ResponseEntity<ErroDTO> cpfUtilizado(RuntimeException e) {
        ErroDTO erro = new ErroDTO(
            HttpStatus.CONFLICT.value(),
            "Conflito de CPF",
            e.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    public ResponseEntity<ErroDTO> emailUtilizado(RuntimeException e) {
        ErroDTO erro = new ErroDTO(
            HttpStatus.CONFLICT.value(),
            "Conflito de E-MAIL",
            e.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
}
