package com.monsterbank.ms_account;

import com.monsterbank.ms_account.account.accountDTOs.CreateAccountRequest;
import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.account.accountDTOs.AccountLastVersion;
import com.monsterbank.ms_account.operations.ExtratoEntity;
import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;

import com.monsterbank.ms_account.operations.operationDTOs.*;
import com.monsterbank.ms_account.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.monsterbank.ms_account.events.*;

import java.util.Optional;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/account")
public class AccountController {

     private AccountService accountService;

     public  AccountController(AccountService as){
          this.accountService = as;
     }

     @GetMapping("/health")
     public ResponseEntity<Void> test() {
          System.out.println("MSACCOUNT FUNFANDO FI");
          return ResponseEntity.ok().build();
     }

     //vou deixar aqui só por encargo de conciencia por enquanto
     @GetMapping("/getMockAcc")
     public ResponseEntity<Optional<AccountDTO>> getMockAcc(){
          return ResponseEntity.ok(this.accountService.getMockAcc());
     }

     @GetMapping 
     public ResponseEntity<List<AccountDTO>> listContas(){
          return ResponseEntity.ok(this.accountService.listaContas());
     }

     @GetMapping("/findByCpf/{cpf}")
     public ResponseEntity<AccountDTO> findAccountByCpf(@PathVariable("cpf") String cpf){
          return ResponseEntity.ok(this.accountService.findAccountByCpf(cpf));
     }

     @GetMapping("/findByNumber/{number}")
     public ResponseEntity<AccountDTO> findAccountByNumber(@PathVariable("number") String number){
          return ResponseEntity.ok(this.accountService.findAccountByNumber(number));
     }

     @GetMapping("/getLastVersion/{number}") //PEGA ULTIMO EVENTO QUE ACONTECEU (SALDO E GERENTE ATUALIZADO)
     public ResponseEntity<AccountLastVersion> getLastVersion(@PathVariable("number") String number){
          return ResponseEntity.ok(this.accountService.getLastVersion(number));
     }

     @GetMapping("/listExtratos/{number}")
     public ResponseEntity <List<ExtratoDTO>> listExtratos(@PathVariable("number") String number){
          return ResponseEntity.ok(this.accountService.listExtratos(number));
     }

     @PostMapping("/create")
     public ResponseEntity<AccountDTO> createAccount(@RequestBody CreateAccountRequest request){
          return ResponseEntity.ok(this.accountService.createAccount(
                         request.getCpf(),
                         request.getManagerId()
                    )
               );
     }

     // @PostMapping("/operate")
     // public ResponseEntity<ExtratoDTO> operate(@RequestBody OperationDTO op){
     //      return ResponseEntity.ok(this.accountService.operate(op));
     // }

     @PostMapping("/sacar")
     public ResponseEntity<Void> sacar(@RequestBody OperationDTO op){
          this.accountService.sacar(op);
          //PRIMEIRO FAZ A OPERAÇÃO QUE E DPS RETORNA VOID, O RESPONSEENTITY NÃO PODE ESPERAR VOID
          return ResponseEntity.ok().build();
     }

     @PostMapping("/depositar")
     public ResponseEntity<Void> depositar(@RequestBody OperationDTO op){
          this.accountService.depositar(op);
          //PRIMEIRO FAZ A OPERAÇÃO QUE E DPS RETORNA VOID, O RESPONSEENTITY NÃO PODE ESPERAR VOID
          return ResponseEntity.ok().build();
     }

     @PostMapping("/transfer")
     public ResponseEntity<ExtratoDTO> transfer(@RequestBody TransferenceDTO t){
          this.accountService.transfer(t);
          //PRIMEIRO FAZ A TRANSF QUE E DPS RETORNA VOID, O RESPONSEENTITY NÃO PODE ESPERAR VOID
          return ResponseEntity.ok().build();
     }
}
