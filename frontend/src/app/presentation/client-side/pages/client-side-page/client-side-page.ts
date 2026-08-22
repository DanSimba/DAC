import { Component, computed, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ClientNavBar } from '../../components/client-nav-bar/client-nav-bar';
import { ClientService } from '../../../../application/client/services/client-service';
import { ClientMenu } from '../../components/client-menu/client-menu';

@Component({
  selector: 'app-client-side-page',
  imports: [RouterOutlet, ClientNavBar, ClientMenu],
  templateUrl: './client-side-page.html',
  styleUrl: './client-side-page.css',
})
export class ClientSidePage {
  clientService = inject(ClientService);
  imo = computed(()=>{
    return this.clientService.getIMO();
  })
}
