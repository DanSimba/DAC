import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { MatIconModule } from '@angular/material/icon';
import { ClientService } from '../../../../application/client/services/client-service';
import { RouterLink } from "@angular/router";
import { AccountService } from '../../../../application/account/services/account-service';

@Component({
  selector: 'app-client-home-page',
  imports: [MatIconModule, RouterLink],
  templateUrl: './client-home-page.html',
  styleUrl: './client-home-page.css',
})
export class ClientHomePage implements OnInit{
  clientService = inject(ClientService)

  client = signal<Client>(this.clientService.getClient());
  accountService = inject(AccountService);
  account = computed(():Account =>{
    return this.accountService.getAccount();
  })

  //TODA VEZ QUE PASSA POR ESSA TELA, PUXA O SALDO ATUALIZADO DNV
  ngOnInit(): void {
    this.accountService.getLastVersion();
  }
}
