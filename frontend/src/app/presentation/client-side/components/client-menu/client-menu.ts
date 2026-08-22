import { Component, signal, inject } from '@angular/core';
import { Client } from '../../../../models/client.model';
import { UserType } from '../../../../enumeration/user-type';
import { ClientStatus } from '../../../../enumeration/client-status';
import { Account } from '../../../../models/account.model';
import { ManagerStatus } from '../../../../enumeration/manager-status';
import { MatIconModule } from '@angular/material/icon';
import { ClientService } from '../../../../application/client/services/client-service';
import { Router } from '@angular/router';
import { sign } from 'crypto';

@Component({
  selector: 'app-client-menu',
  imports: [],
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
