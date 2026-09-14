import { inject, Injectable } from '@angular/core';
import { OperationModel } from '../../domain/operations/models/operation.model';
import { Observable } from 'rxjs';
import { Account } from '../../domain/account/models/account.model';
import { TransferenceModel } from '../../domain/operations/models/transference.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AccountHttpService {
  http = inject(HttpClient);
  //private readonly API_URL = environment.apiUrl;


  //FAZ A OPERAÇÃO CERTA E AI RETORNA A CONTA COM O VALOR ATUALIZADO
  operar(op: OperationModel): Observable<Account>{
    return this.http.post<Account>('URL DESSA BOSTA', op);
  }

  //RETORNA A ACCOUNT COM O SALDO ATUALIZADO
  //SE NÃO ENCONTRAR O DESTINATÁRIO, DEVE RETORNAR ERROR 
  transferir(t: TransferenceModel): Observable<Account>{
    return this.http.post<Account>('URL DESSA BOSTA', t);
  }
}
