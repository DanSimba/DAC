package com.monsterbank.ms_account;

import com.monsterbank.ms_cliente.cliente.ClienteService;
import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.listarSolicitacoesReturn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;

@RestController
@RequestMapping("/account")
public class AccountController {

}
