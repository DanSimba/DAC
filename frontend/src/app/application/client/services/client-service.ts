import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../../../models/client.model';
import { ClientStatus } from '../../../enumeration/client-status';
import { UserType } from '../../../enumeration/user-type';
import { Account } from '../../../models/account.model';
import { ManagerStatus } from '../../../enumeration/manager-status';
import { OperationModel } from '../../../models/operation.model';
import { ClientHttpService } from '../../../infraestructure/http/client.http.service';
import { Observable, tap } from 'rxjs';
import { TransferenceModel } from '../../../models/transference.model';
import { CreateClient } from '../../../models/create-client.model';

@Injectable({
  providedIn: 'root',
})
export class ClientService {

  private clientHttpService = inject(ClientHttpService);
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

  private account = signal<Account>({
      client   : this.clientMock(),
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
    return this.clientMock();
  }
  
  getAccount():Account{
    return this.account();
  }

  setClient(c:Client){
    this.clientMock.set(c);
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
    return this.clientHttpService.operar(op).pipe(
      tap({
        next: (response)=>{
          this.account.set(response)
        },
        error: (err)=>{
          console.log('err: ', err);
        }
      })
    )
  }

  transferir(t: TransferenceModel): Observable<Account>{
    return this.clientHttpService.transferir(t).pipe(
      tap({
        next: (response)=>{
          this.account.set(response)
        },
        error: (err)=>{
          console.log('err: ', err);
        }
      })
    )
  }

  //RESOLVI FZR UMA FUNÇÃO PRA PEGAR A DATA E HORA DO JEITO QUE O RAZER GOSTA AUTOMATICAMENTE
  getCurrentTimeFormated(): string{
    const date = new Date();
    //console.log('DATA NÃO FORMATADA: ', date);

    const formatedDate = `${date.getDate()}/${date.getMonth()+1}/${date.getFullYear()} ${date.getHours()}:${date.getMinutes()}`
    console.log('DATETIME AGR: ', formatedDate);

    return formatedDate;
  }

  // Envia o client recebido do component para o httpService fazer a solicitação ao API Gateway
  createClientRequestclient(clientRequest: CreateClient): Observable<void> {
    return this.clientHttpService.createClientRequestclient(clientRequest);
  }

}
