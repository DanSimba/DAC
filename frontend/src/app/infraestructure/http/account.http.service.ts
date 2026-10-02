import { inject, Injectable } from '@angular/core';
import { OperationModel } from '../../domain/operations/models/operation.model';
import { Observable } from 'rxjs';
import { Account } from '../../domain/account/models/account.model';
import { TransferenceModel } from '../../domain/operations/models/transference.model';
import { HttpClient } from '@angular/common/http';
import { environmentDev } from '../../../enviroments/enviroment.development';
import { ExtratoModel } from '../../domain/operations/models/extrato.model';

@Injectable({
  providedIn: 'root',
})
export class AccountHttpService {
  http = inject(HttpClient);
  private readonly API_URL = `${environmentDev.apiUrlAccount}/account`;


  //FAZ A OPERAÇÃO CERTA E AI RETORNA A CONTA COM O VALOR ATUALIZADO
  operar(op: OperationModel): Observable<ExtratoModel>{
    return this.http.post<ExtratoModel>(`${this.API_URL}/operate`, op);
  }

  //RETORNA A ACCOUNT COM O SALDO ATUALIZADO
  //SE NÃO ENCONTRAR O DESTINATÁRIO, DEVE RETORNAR ERROR 
  transferir(t: TransferenceModel): Observable<ExtratoModel>{
    return this.http.post<ExtratoModel>(`${this.API_URL}/transfer`, t);
  }
}
