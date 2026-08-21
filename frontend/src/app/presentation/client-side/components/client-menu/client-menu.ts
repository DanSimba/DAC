import { Component, inject } from '@angular/core';
import { ClientService } from '../../../../application/client/services/client-service';

@Component({
  selector: 'app-client-menu',
  imports: [],
  templateUrl: './client-menu.html',
  styleUrl: './client-menu.css',
})
export class ClientMenu {
  clienService = inject(ClientService)
}
