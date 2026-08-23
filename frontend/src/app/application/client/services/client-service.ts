import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../../../models/client.model';
import { ClientStatus } from '../../../enumeration/client-status';
import { UserType } from '../../../enumeration/user-type';
import { Account } from '../../../models/account.model';
import { ManagerStatus } from '../../../enumeration/manager-status';
import { OperationModel } from '../../../models/operation.model';
import { ClientHttpService } from '../../../infraestructure/http/client.http.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ClientService {

  clientHttpService = inject(ClientHttpService);
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

  getClient():Client{
    return this.client();
  }
  
  getAccount():Account{
    return this.account();
  }

  setClient(c:Client){
    this.client.set(c);
  }

  setAccount(a:Account){
    this.account.set(a);
  }

  private isMenuOpen = signal<boolean>(false);
  toggleMenu(){
    this.isMenuOpen.set(!this.isMenuOpen());
  }

  getIMO(): boolean{
    return this.isMenuOpen()
  }

  operar(op: OperationModel): Observable<Account>{
    return this.clientHttpService.operar(op);
  }
}
