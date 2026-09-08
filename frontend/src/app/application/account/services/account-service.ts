import { inject, Injectable, signal } from '@angular/core';
import { Account } from '../../../domain/account/models/account.model';
import { ExtratoService } from '../../extrato/services/extrato-service';
import { ClientService } from '../../client/services/client-service';
import { Client } from '../../../domain/client/models/client.model';
import { ManagerStatus } from '../../../enumeration/manager-status';
import { UserType } from '../../../enumeration/user-type';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { Observable, tap } from 'rxjs';
import { AccountHttpService } from '../../../infraestructure/http/account.http.service';
import { OperationModel } from '../../../domain/operations/models/operation.model';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private extratoService = inject(ExtratoService);
  private clientService = inject(ClientService);
  private accountHttpService = inject(AccountHttpService)

  client = signal<Client>(this.clientService.getClient());

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

  getAccount():Account{
    return this.account();
  }

  setAccount(a:Account){
    this.account.set(a);
  }

    operar(op: OperationModel): Observable<Account>{
      //LÓGICA DE ADICIONAR EXTRATO, DEVE IR PARA DENTRO DO SUBSCRIBE DEPOIS
        //pega a data em forma de id
        const now = new Date();
        //ganbiarra pra tranformar Date no id formato AAAAMMDD
        const nowId = +`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
        
        this.extratoService.createExtrato(op, nowId, this.account().balance);
  
      return this.accountHttpService.operar(op).pipe(
        tap({
          next: (response)=>{
            this.account.set(response);
          },
          error: (err)=>{
            console.log('err: ', err);
          }
        })
      )
    }
  
    transferir(t: TransferenceModel): Observable<Account>{
      //LÓGICA DE ADICIONAR EXTRATO, DEVE IR PARA DENTRO DO SUBSCRIBE DEPOIS
        //pega a data em forma de id
        const now = new Date();
        //ganbiarra pra tranformar Date no id formato AAAAMMDD
        const nowId = +`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
        
        this.extratoService.createExtrato(t, nowId, this.account().balance);
  
      return this.accountHttpService.transferir(t).pipe(
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
      //console.log('DATETIME AGR: ', formatedDate);
  
      return formatedDate;
    }
}
