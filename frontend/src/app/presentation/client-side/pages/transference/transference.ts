import { Component, inject, signal } from '@angular/core';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../models/client.model';
import { Account } from '../../../../models/account.model';

@Component({
  selector: 'app-transference',
  imports: [],
  templateUrl: './transference.html',
  styleUrl: './transference.css',
})
export class Transference {
  clientService = inject(ClientService);
  client = signal<Client>(this.clientService.getClient());
  account = signal<Account>(this.clientService.getAccount());
}
