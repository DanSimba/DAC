import { Component, inject, signal } from '@angular/core';
import { Client } from '../../../../models/client.model';
import { Account } from '../../../../models/account.model';
import { MatIconModule } from '@angular/material/icon';
import { ClientService } from '../../../../application/client/services/client-service';

@Component({
  selector: 'app-client-home-page',
  imports: [MatIconModule],
  templateUrl: './client-home-page.html',
  styleUrl: './client-home-page.css',
})
export class ClientHomePage {
  clientService = inject(ClientService)

  client = signal<Client>(this.clientService.getClient());
  account = signal<Account>(this.clientService.getAccount());
}
