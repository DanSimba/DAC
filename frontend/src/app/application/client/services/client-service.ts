import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../../../domain/client/models/client.model';
import { ClientStatus } from '../../../enumeration/client-status';
import { UserType } from '../../../enumeration/user-type';
import { ClientHttpService } from '../../../infraestructure/http/client.http.service';
import { ExtratoService } from '../../extrato/services/extrato-service';
import { Observable } from 'rxjs';

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
          logradouro   : 'a',
          numero       : 'a',
          complemento  : 'a',
          bairro       : 'a',
          cidade       : 'a',
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

  createClientRequest(payload: any): Observable<void>{
    return this.clientHttpService.createClientRequest(payload);
  }
}
