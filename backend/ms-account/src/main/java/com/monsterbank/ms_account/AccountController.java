package com.monsterbank.ms_account;

import com.monsterbank.ms_account.Account.AccountDTOs.CreateAccountRequest;
import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
import com.monsterbank.ms_account.operations.ExtratoEntity;
import com.monsterbank.ms_account.operations.OperationEntity;
import com.monsterbank.ms_account.operations.TransferenceEntity;
import com.monsterbank.ms_account.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

     private AccountService accountService;

     public  AccountController(AccountService as){
          this.accountService = as;
     }

     @GetMapping("/test")
     public String test() {
          System.out.println("MSACCOUNT FUNFANDO FI");
          return "ms-account funcionando!";
     }

     @GetMapping("/findByCpf/{cpf}")
     public ResponseEntity<Optional<AccountDTO>> findAccountByCpf(@PathVariable("cpf") String cpf){
          return ResponseEntity.ok(this.accountService.findAccountByCpf(cpf));
     }

     @GetMapping("/findByNumber/{number}")
     public ResponseEntity<Optional<AccountDTO>> findAccountByNumber(@PathVariable("number") String number){
          return ResponseEntity.ok(this.accountService.findAccountByNumber(number));
     }

     @GetMapping("/listExtratos/{number}")
     public ResponseEntity<Optional<ExtratoDTO[]>> listExtratos(@PathVariable("number") String number){
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

     @PostMapping("/operate")
     public ResponseEntity<ExtratoDTO> operate(@RequestBody OperationEntity op){
          return ResponseEntity.ok(this.accountService.operate(op));
     }

     @PostMapping("/transfer")
     public ResponseEntity<ExtratoDTO> transfer(@RequestBody TransferenceEntity t){
          return ResponseEntity.ok(this.accountService.transfer(t));
     }
}
