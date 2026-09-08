import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../../../domain/client/models/client.model';
import { ClientStatus } from '../../../enumeration/client-status';
import { UserType } from '../../../enumeration/user-type';
import { Account } from '../../../domain/account/models/account.model';
import { ManagerStatus } from '../../../enumeration/manager-status';
import { OperationModel } from '../../../domain/operations/models/operation.model';
import { ClientHttpService } from '../../../infraestructure/http/client.http.service';
import { Observable, tap } from 'rxjs';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';
import { CreateClient } from '../../../domain/client/models/create-client.model';
import { ExtratoService } from '../../extrato/services/extrato-service';

@Injectable({
  providedIn: 'root',
})
export class ClientService {

  private clientHttpService = inject(ClientHttpService);
  private extratoService = inject(ExtratoService);
  //MOCKZIN
  private clientMock = signal<Client>({
        id       : 1,
        cpf      : '00011122233',
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

  //CONTROLE DE LOGGADO OU NÃO
  logged = signal<boolean>(true);

  getClient():Client{
    return this.clientMock();
  }

  setClient(c:Client){
    this.clientMock.set(c);
  }

  private isMenuOpen = signal<boolean>(false);
  toggleMenu(){
    this.isMenuOpen.set(!this.isMenuOpen());
  }

  getIMO(): boolean{
    return this.isMenuOpen()
  }
}
