import { inject, Injectable, signal } from '@angular/core';
import { Account } from '../../../domain/account/models/account.model';
import { ExtratoService } from '../../extrato/services/extrato-service';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { map, Observable, tap } from 'rxjs';
import { AccountHttpService } from '../../../infraestructure/http/account.http.service';
import { OperationModel } from '../../../domain/operations/models/operation.model';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';
import { response } from 'express';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private extratoService = inject(ExtratoService);
  private accountHttpService = inject(AccountHttpService)

  private account = signal<Account>({
      client_cpf: '00011122233',
      number   : '001',
      balanco  : 1000,
      manager_id  : 2
  })

  getAccount():Account{
    console.log("CONTA no service: ", this.account());
    return this.account();
  }

  setAccount(a:Account){
    this.account.set(a);
  }

  getMockAcc(){
    this.accountHttpService.getMockAcc().subscribe({
      next:(response)=>{
        this.account.set(response);
        console.log("conta: ", response);
      }
    })
  }

  findAccountByCpf(cpf: string){
    this.accountHttpService.findAccountByCpf(cpf).subscribe({
      next:(response)=>{
        this.account.set(response);
        //console.log("conta: ", response);
      }
    })
  }

  findAccountByNumber(number: string){
    this.accountHttpService.findAccountByNumber(number).subscribe({
      next:(response)=>{
        this.account.set(response);
        //console.log("conta: ", response);
      }
    })
  }

  //IDEIA DE REFATORAÇÃO: AO OPERAR E TRANFSERIR RETORNA A LISTA DE EXTRATO COMPLETA DEVOLTA
    operar(op: OperationModel): Observable<Account>{

      /*OPÇAO PROVISORIA QUE CRIA O EXTRATO DIRETO NO FRONT E MANDA LA PRA LISTA
          //LÓGICA DE ADICIONAR EXTRATO, DEVE IR PARA DENTRO DO SUBSCRIBE DEPOIS
            //pega a data em forma de id
            const now = new Date();
            //ganbiarra pra tranformar Date no id formato AAAAMMDD
            const nowId = +`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
            
            this.extratoService.createExtrato(op, nowId, this.account()); //QNDO ESTIVER DENTRO DO SUBSCRIBE(), PODE METER DIRETO O RESPONSE
      */
      return this.accountHttpService.operar(op).pipe(
        map((response) => {
        
          this.extratoService.addToExtList(response); //ATUALIZA EXTLIST

          this.account.update(current => ({
            ...current,  
            balanco: response.saldoApos //ATUALIZA BALANCO
          }));

          return this.account(); 
        })
      )
    }
  
    transferir(t: TransferenceModel): Observable<Account>{
      
        /*OPÇAO PROVISORIA QUE CRIA O EXTRATO DIRETO NO FRONT E MANDA LA PRA LISTA
          //LÓGICA DE ADICIONAR EXTRATO, DEVE IR PARA DENTRO DO SUBSCRIBE DEPOIS
            //pega a data em forma de id
            const now = new Date();
            //ganbiarra pra tranformar Date no id formato AAAAMMDD
            const nowId = +`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
            
            this.extratoService.createExtrato(t, nowId, this.account());
        */
      return this.accountHttpService.transferir(t).pipe(
        map((response) => {
        
          this.extratoService.addToExtList(response); //ATUALIZA EXTLIST

          this.account.update(current => ({
            ...current,  
            balanco: response.saldoApos //ATUALIZA BALANCO
          }));

          return this.account(); 
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
