import { inject, Injectable, signal } from '@angular/core';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';
import { OperationModel } from '../../../domain/operations/models/operation.model';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { Account } from '../../../domain/account/models/account.model';

@Injectable({
  providedIn: 'root',
})
export class ExtratoService {

  private extList = signal<ExtratoModel[]> ([]);

  createExtrato(ext: OperationModel|TransferenceModel, dateId: number, acc: Account){ 
      //procura por esse id no extrato (se já tem o dia, coloca ext lá)
        const extDay = this.extList().find(day => day.dateId==dateId)
        if(extDay){
            if(ext.type == 'operation'){
                extDay.opers.push(ext);
                extDay.saldoApos = acc.balance;
                console.log('extrato atualizado: ', this.extList());
            } else{
                extDay.transfs.push(ext);
                extDay.saldoApos = acc.balance;
                console.log('extrato atualizado: ', this.extList());
            }
            return
        }
        
      //caso não ache o dia (é o primeiro extrato do dia), cria um novo
        const newExt: ExtratoModel = {
          acc_number: acc.number,
          transfs: [],
          opers: [],
          id: Math.random()*1000,
          dateId: dateId,
          saldoApos: acc.balance //JA ESTA ATUALIZADO NA FUNÇÃO DE OPERAR()/TRANFERIR()
        }
  
        if(ext.type == 'operation'){
          newExt.opers.push(ext);
        } else{
          newExt.transfs.push(ext);
        }
  
        this.extList.update(exts=>[newExt, ...exts]);
        console.log('extrato atualizado: ', this.extList());
  }
  
  createEmptyDay(dateId: number, acc: Account){
    //encontra ultimo saldo
    //deus proteja quem tiver que debuggar isso aq
    let lastSaldo = this.extList().find((ext)=>(ext.dateId < dateId))?.saldoApos ?? acc.balance;
    const emptyDay: ExtratoModel = {
       acc_number: acc.number,
          transfs: [],
          opers: [],
          id: Math.random()*1000,
          dateId: dateId,
          saldoApos: lastSaldo //JA ESTA ATUALIZADO NA FUNÇÃO DE OPERAR()/TRANFERIR()
    } 

    this.extList.update(exts=>[emptyDay, ...exts]);
    //console.log('extrato atualizado com dia vazio: ', this.extList());
  }

  getExtList():ExtratoModel[]{
    return this.extList();
  }

  createMonthExt(acc: Account){ //roda por tds os dias do mÊs adicionando extratos á lista
    const now = new Date();
    let days = now.getDate();

    while(days>0){
      let idString = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(days).padStart(2, '0')}`
      let dateId = this.createDateId(idString);

      //miseria.ts
      //encontra a ultima oper / transf na lista antes do dia vazio
      //let lastOp = this.extList().find((ext)=>(ext.opers.length>0||ext.tranfs.length>0)&&ext.id<id);

      //procura se o dia ja consta na extList
      let existingDay = this.extList().find((ext)=>(ext.dateId == dateId));
      if(!existingDay){ //SE O DIA NAO EXISTE, CRIA VAZIO
         this.createEmptyDay(dateId, acc);
      }
      days--
    }
  }


  //--------FUNÇÕES DE DATA----#$%¨&*)(*&¨%$#)

  createDateId(d:string):number{ //d = 'aaaa-mm-dd'
    //pega a string e tira os traços
    const dateInt = d.split("-");
    //console.log('dateInt: ', dateInt);

    return +(dateInt[0]+dateInt[1]+dateInt[2]);
  }
  
  //FAZ LITERALMENTE O CONTRARIO DA FUNÇÃO DE CIMA 
  formatDateId(d:number):string{
    const dString = d.toString();
    //console.log('DSTRING: ', dString)
    const formatedDate = `${dString.slice(6,8)}/${dString.slice(4,6)}/${dString.slice(0,4)}`
    return formatedDate;
  }
}
