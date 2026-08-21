import { Component, signal } from '@angular/core';
import { Client } from '../../../../models/client.model';
import { UserType } from '../../../../enumeration/user-type';
import { ClientStatus } from '../../../../enumeration/client-status';
import { Account } from '../../../../models/account.model';
import { ManagerStatus } from '../../../../enumeration/manager-status';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-client-home-page',
  imports: [MatIconModule],
  templateUrl: './client-home-page.html',
  styleUrl: './client-home-page.css',
})
export class ClientHomePage {
  //MOCKZIN
  client = signal<Client>({
        id       : 1,
        cpf      : '000111222-33',
        name     : 'razerson nvidio da silva',
        email    : 'razer@gmail.com',
        password : 'starwars123',
        salary   : 20000,
        address  : {
          cep          : 'a',
          street      : 'a',
          number       : 'a',
          complement   : 'a',
          neighborhood : 'a',
          city         : 'a',
          uf           : 'a',
          state        : 'a',
        },
        status   : ClientStatus.ACTIVE,
        type     : UserType.CLIENT,
  });

  account = signal<Account>({
        client   : this.client(),
        number   : '001',
        balance  : 1000,
        manager  : {
              id       : 2,
              name     : 'dievalson oracle pereira',
              cpf      : 'dievalson oracle pereira',
              email    : 'dievalson oracle pereira',
              password : 'dievalson oracle pereira',
              status   : ManagerStatus.ACTIVE,
              type     : UserType.MANAGER
        },
        createdAt : '10/10/2012',
  })
}
