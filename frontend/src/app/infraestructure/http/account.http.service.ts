import { inject, Injectable } from '@angular/core';
import { OperationModel } from '../../domain/operations/models/operation.model';
import { Observable } from 'rxjs';
import { Account } from '../../domain/account/models/account.model';
import { TransferenceModel } from '../../domain/operations/models/transference.model';
import { HttpClient } from '@angular/common/http';
import { environmentDev } from '../../../enviroments/enviroment.development';
import { ExtratoModel } from '../../domain/operations/models/extrato.model';
import { AccountLastVersionModel } from '../../domain/account/models/account-last-version.model';

@Injectable({
  providedIn: 'root',
})
export class AccountHttpService {
  http = inject(HttpClient);
  private readonly API_URL = `${environmentDev.apiUrlAccount}/account`;

  findAccountByCpf(cpf:string): Observable<Account>{
    return this.http.get<Account>(`${this.API_URL}/findByCpf/${cpf}`);
  }

  findAccountByNumber(number:string): Observable<Account>{
    return this.http.get<Account>(`${this.API_URL}/findByNumber/${number}`);
  }

  listExtratos(number:string): Observable<ExtratoModel[]>{
    return this.http.get<ExtratoModel[]>(`${this.API_URL}/listExtratos/${number}`);
  }

  //FAZ A OPERAÇÃO E NÃO RETORNA A ACC, O FRONT PRECISA PEDIR DENOVO E EXTRATO COM GETLASTVERSION
  //NOS REQS DIZ QUE NÃO TEM PROBLEMA TER ESSE PEQUENO ATRASO ATÉ ATUALIZAR O BALANCO 
  sacar(op: OperationModel): Observable<void>{
    return this.http.post<void>(`${this.API_URL}/sacar`, op);
  }

  depositar(op: OperationModel): Observable<void>{
    return this.http.post<void>(`${this.API_URL}/depositar`, op);
  }

  getLastVersion(number: string): Observable<AccountLastVersionModel>{
    return this.http.get<AccountLastVersionModel>(`${this.API_URL}/getLastVersion/${number}`);
  }

  //RETORNA A ACCOUNT COM O SALDO ATUALIZADO
  //SE NÃO ENCONTRAR O DESTINATÁRIO, DEVE RETORNAR ERROR 
  transferir(t: TransferenceModel): Observable<void>{
    return this.http.post<void>(`${this.API_URL}/transfer`, t);
  }
}
