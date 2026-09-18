package com.monsterbank.ms_account;

import com.monsterbank.ms_account.Account.AccountDTOs.CreateAccountRequest;
import com.monsterbank.ms_account.account.accountDTOs.AccountDTO;
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

     @GetMapping("/findByCpf")
     public ResponseEntity<Optional<AccountDTO>> findAccountByCpf(@RequestParam String cpf){
          return ResponseEntity.ok(this.accountService.findAccountByCpf(cpf));
     }

     @GetMapping("/findByNumber")
     public ResponseEntity<Optional<AccountDTO>> findAccountByNumber(@RequestParam String number){
          return ResponseEntity.ok(this.accountService.findAccountByNumber(number));
     }

     @PostMapping("/create")
     public ResponseEntity<AccountDTO> createAccount(@RequestBody CreateAccountRequest request){
          return ResponseEntity.ok(this.accountService.createAccount(
                         request.getCpf(),
                         request.getManagerId()
                    )
               );
     }
}
