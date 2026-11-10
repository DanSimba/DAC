package com.monsterbank.ms_account;

import com.monsterbank.ms_account.Account.AccountDTOs.CreateAccountRequest;
import com.monsterbank.ms_account.Account.AccountDTOs.AccountDTO;
import com.monsterbank.ms_account.Account.AccountDTOs.AccountLastVersion;
import com.monsterbank.ms_account.operations.ExtratoEntity;
import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;

import com.monsterbank.ms_account.operations.operationDTOs.*;
import com.monsterbank.ms_account.Account.AccountService;
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
     public ResponseEntity<ExtratoDTO> sacar(@RequestBody OperationDTO op){
          return ResponseEntity.ok(this.accountService.sacar(op));
     }

     @PostMapping("/depositar")
     public ResponseEntity<ExtratoDTO> depositar(@RequestBody OperationDTO op){
          return ResponseEntity.ok(this.accountService.depositar(op));
     }

     @PostMapping("/transfer")
     public ResponseEntity<ExtratoDTO> transfer(@RequestBody TransferenceDTO t){
          return ResponseEntity.ok(this.accountService.transfer(t));
     }
}
