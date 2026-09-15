package com.monsterbank.ms_account;

import com.monsterbank.ms_account.Account.AccountDTOs.CreateAccountRequest;
import com.monsterbank.ms_account.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;

    private AccountController(AccountService as){
        this.accountService = as;
    }

    @GetMapping("/findByCpf")
    public ResponseEntity<AccountDTO> findAccountByCpf(@RequestParam String cpf){
         return ResponseEntity.ok(this.accountService.findAccountByCpf(cpf));
    }

    @GetMapping("/findByNumber")
    public ResponseEntity<AccountDTO> findAccountByNumber(@RequestParam String number){
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
