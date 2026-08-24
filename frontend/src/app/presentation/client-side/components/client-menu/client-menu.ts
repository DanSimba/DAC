import { Component, signal, inject } from '@angular/core';
import { Client } from '../../../../models/client.model';
import { Account } from '../../../../models/account.model';
import { ClientService } from '../../../../application/client/services/client-service';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-client-menu',
  imports: [MatIconModule, RouterLink],
  templateUrl: './client-menu.html',
  styleUrl: './client-menu.css',
})
export class ClientMenu {
  clientService = inject(ClientService)
  private router = inject(Router);

  client = signal<Client>(this.clientService.getClient());
  account = signal<Account>(this.clientService.getAccount());

  logout(){
    console.log('usuário saiu!!');
    this.router.navigate([''])
  }
}
