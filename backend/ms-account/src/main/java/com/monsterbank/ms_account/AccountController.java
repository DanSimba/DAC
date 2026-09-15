package com.monsterbank.ms_account;

// ms-cliente comentado pq o ms-account não deve conhecer o ms-cliente
// comunicação deve ser feita via mensageria
/* 
    import com.monsterbank.ms_cliente.cliente.ClienteService;
    import com.monsterbank.ms_cliente.solicitacao.SolicitacaoService;
    import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RegistrarSolicitacaoRequest;
    import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.listarSolicitacoesReturn;
    import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;
    import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.RecusarSolicitacaoRequest;
*/

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/account")
public class AccountController {

}
