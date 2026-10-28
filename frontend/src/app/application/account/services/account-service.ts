import { inject, Injectable, signal } from '@angular/core';
import { Account } from '../../../domain/account/models/account.model';
import { ExtratoService } from '../../extrato/services/extrato-service';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { map, Observable, tap } from 'rxjs';
import { AccountHttpService } from '../../../infraestructure/http/account.http.service';
import { OperationModel } from '../../../domain/operations/models/operation.model';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private extratoService = inject(ExtratoService);
  private accountHttpService = inject(AccountHttpService)

  private account = signal<Account>({
      client_cpf: '',
      number   : '',
      balanco  : 0,
      manager_id  : 0,
      createdAt: 9999
  })

  getAccount():Account{
    //console.log("CONTA no service: ", this.account());
    return this.account();
  }

  setAccount(a:Account){
    this.account.set(a);
    console.log("conta: ", this.account());
  }

  getMockAcc(){
    this.accountHttpService.getMockAcc().subscribe({
      next:(response)=>{
        this.account.set(response);
        //console.log("conta getmockAcc: ", response);
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
        console.log("conta: ", response);
        
        //JÁ BUSCA O EXTRATO LOGO EM SEGUIDA CASO ESTEJA NA TELA EXTRATO
        this.listExtratos();
      },
      error: (err) => {
        console.log("Conta não encontrada!!! :(")
         this.account.update(current => ({
            ...current,  
            number: 'X'
          }));
      }
    })
  }

  //IDEIA DE REFATORAÇÃO: AO OPERAR E TRANFSERIR RETORNA A LISTA DE EXTRATO COMPLETA DEVOLTA
    operar(op: OperationModel): Observable<Account>{

      return this.accountHttpService.operar(op).pipe(
        map((response) => {

          console.log("EXTRATO CRIADO: ", response);
        
          this.extratoService.addToExtList(response); //ATUALIZA EXTLIST
          this.extratoService.addToRealList(response); //ATUALIZA LISTA CRUA

          this.account.update(current => ({
            ...current,  
            balanco: response.saldoApos //ATUALIZA BALANCO
          }));

          return this.account(); 
        })
      )
    }
  
    transferir(t: TransferenceModel): Observable<Account>{
      return this.accountHttpService.transferir(t).pipe(
        map((response) => {
          console.log("EXTRATO VINDO DO BACK: ", response)
          this.extratoService.addToExtList(response); //ATUALIZA EXTLIST
          this.extratoService.addToRealList(response); //ATUALIZA LISTA CRUA

          this.account.update(current => ({
            ...current,  
            balanco: response.saldoApos //ATUALIZA BALANCO
          }));

          return this.account(); 
        })
      )
    }

  listExtratos(){ //já caotura, atualiza o balanco e coloca tudo na extList
    this.accountHttpService.listExtratos(this.account().number).subscribe({
      next:(response)=>{
        this.extratoService.setExtList(response);
        this.extratoService.setRealList(response);
        if(response.length>0){
          this.account.update(current => ({
            ...current,  
            balanco: response[response.length-1].saldoApos //ATUALIZA BALANCO
          }));
        }
        
        //console.log('ACCOUNT QUE VAI PRA CREATEMONTH: ', this.account())
        //POPULA A LISTA DE EXTRATO COM OS DIAS VAZIOS
        //this.extratoService.createMonthExt(this.account()); //TROCADO PRA O COMP EXTRATO
        console.log("lista de exts: ", response);
      }
    })
  }
  
    //RESOLVI FZR UMA FUNÇÃO PRA PEGAR A DATA E HORA DO JEITO QUE O RAZER GOSTA AUTOMATICAMENTE
    getCurrentTimeFormated(): string{
      const date = new Date();
      //console.log('DATA NÃO FORMATADA: ', date);
  
      const formatedDate = `${date.getDate()}/${date.getMonth()+1}/${date.getFullYear()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
      //console.log('DATETIME AGR: ', formatedDate);
  
      return formatedDate;
    }
}
