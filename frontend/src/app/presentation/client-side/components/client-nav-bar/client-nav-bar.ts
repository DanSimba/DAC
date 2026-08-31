import { Component, inject } from '@angular/core';
import { ClientService } from '../../../../application/client/services/client-service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-client-nav-bar',
  imports: [MatIconModule],
  templateUrl: './client-nav-bar.html',
  styleUrl: './client-nav-bar.css',
})
export class ClientNavBar {
  clientService = inject(ClientService)
  
}
