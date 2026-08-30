import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OperationModel } from '../../domain/operations/models/operation.model';
import { Account } from '../../domain/account/models/account.model';
import { Transference } from '../../presentation/client-side/pages/transference/transference';
import { TransferenceModel } from '../../domain/operations/models/transference.model';

@Injectable({
  providedIn: 'root',
})
export class ClientHttpService {
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
