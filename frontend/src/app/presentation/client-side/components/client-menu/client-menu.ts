import { Component, signal, inject, OnInit } from '@angular/core';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { ClientService } from '../../../../application/client/services/client-service';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AccountService } from '../../../../application/account/services/account-service';

@Component({
  selector: 'app-client-menu',
  imports: [MatIconModule, RouterLink],
  templateUrl: './client-menu.html',
  styleUrl: './client-menu.css',
})
export class ClientMenu implements OnInit{
  clientService = inject(ClientService);
  private router = inject(Router);

  client = signal<Client>(this.clientService.getClient());

  private accountService = inject(AccountService);
  account = signal<Account>(this.accountService.getAccount());

  ngOnInit(): void {
    this.accountService.getMockAcc(); //a resposta ja ta subcrita lá no service, ai a account daqui só puxa o resultado pronto de la
    this.account.set(this.accountService.getAccount());
    //console.log("CONTA PUXADA: ", this.account())
  }

  logout(){
    console.log('usuário saiu!!');
    this.router.navigate([''])
  }
}
