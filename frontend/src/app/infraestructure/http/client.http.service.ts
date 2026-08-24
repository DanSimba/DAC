import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OperationModel } from '../../models/operation.model';
import { Account } from '../../models/account.model';

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
}
