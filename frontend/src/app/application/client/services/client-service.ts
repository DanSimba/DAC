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

@Injectable({
  providedIn: 'root',
})
export class ClientService {

  private clientHttpService = inject(ClientHttpService);
  //MOCKZIN
  private client = signal<Client>({
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
}
