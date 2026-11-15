import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { MatIconModule } from '@angular/material/icon';
import { ClientService } from '../../../../application/client/services/client-service';
import { RouterLink } from "@angular/router";
import { AccountService } from '../../../../application/account/services/account-service';
import { AuthHttpService } from '../../../../infraestructure/http/auth.http.service';

@Component({
  selector: 'app-client-home-page',
  imports: [MatIconModule, RouterLink],
  templateUrl: './client-home-page.html',
  styleUrl: './client-home-page.css',
})
export class ClientHomePage implements OnInit{
  clientService = inject(ClientService);
  accountService = inject(AccountService);
  private authHttpService = inject(AuthHttpService);

  client = computed((): Client => {
    return this.clientService.getClient();
  });

  account = computed((): Account => {
    return this.accountService.getAccount();
  });

  //TODA VEZ QUE PASSA POR ESSA TELA, PUXA O SALDO ATUALIZADO DNV
  ngOnInit(): void {
    const usuarioLogado = this.authHttpService.getUsuario();
    if (usuarioLogado?.cpf) {
      if (!this.client().cpf) {
        this.clientService.setClient({
          ...this.clientService.getClient(),
          cpf: usuarioLogado.cpf,
          name: usuarioLogado.nome ?? ''
        });
        this.clientService.findClientByCpf(usuarioLogado.cpf);
      }

      if (!this.account().number) {
        this.accountService.findAccountByCpf(usuarioLogado.cpf);
        return;
      }
    }
    
    if (this.account().number) {
      this.accountService.getLastVersion();
    }
  }
}
