import { Injectable, signal } from '@angular/core';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';
import { OperationModel } from '../../../domain/operations/models/operation.model';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { Account } from '../../../domain/account/models/account.model';

@Injectable({
  providedIn: 'root',
})
export class ExtratoService {

  private extList = signal<ExtratoModel[]> ([]);

  //SOLUÇÃO ALTERNATIVA: PASSAR A LISTA INTEIRA DNV
  setExtList(el: ExtratoModel[]){
    this.extList.set(el);
    //console.log("EXTLIST ESTÁ ASSIM: ", this.extList())
  }

  addToExtList(ext: ExtratoModel){
        this.extList.update(current=>[...current, ext]);
  }

  createExtrato(ext: OperationModel|TransferenceModel, dateId: number, acc: Account){ 
      //procura por esse id no extrato (se já tem o dia, coloca ext lá)
        const extDay = this.extList().find(day => day.dateId==dateId)
        if(extDay){
            if(ext.type == 'operation'){
                extDay.opers.push(ext);
                extDay.saldoApos = acc.balanco;
                console.log('extrato atualizado: ', this.extList());
            } else{
                extDay.transfs.push(ext);
                extDay.saldoApos = acc.balanco;
                console.log('extrato atualizado: ', this.extList());
            }
            return
        }
        
      //caso não ache o dia (é o primeiro extrato do dia), cria um novo
        const newExt: ExtratoModel = {
          accNumber: acc.number,
          transfs: [],
          opers: [],
          dateId: dateId,
          saldoApos: acc.balanco //JA ESTA ATUALIZADO NA FUNÇÃO DE OPERAR()/TRANFERIR()
        }
  
        if(ext.type == 'operation'){
          newExt.opers.push(ext);
        } else{
          newExt.transfs.push(ext);
        }
  
        this.extList.update(exts=>[newExt, ...exts]);
        //console.log('extrato atualizado: ', this.extList());
  }
  
  createEmptyDay(dateId: number, acc: Account){
    //encontra ultimo saldo
    //deus proteja quem tiver que debuggar isso aq
    let lastSaldo = [...this.extList()].reverse().find((ext)=>(ext.dateId < dateId))?.saldoApos ?? 0;
    const emptyDay: ExtratoModel = {
       accNumber: acc.number,
          transfs: [],
          opers: [],
          dateId: dateId,
          saldoApos: lastSaldo //JA ESTA ATUALIZADO NA FUNÇÃO DE OPERAR()/TRANFERIR()
    } 

    this.extList.update(exts => {
      const novaLista = [...exts];
      const index = novaLista.findIndex(item => item.dateId > emptyDay.dateId);
      
      if (index === -1) {
        novaLista.push(emptyDay);
      } else {
        //Se achar, insere exatamente naquela posição
        novaLista.splice(index, 0, emptyDay);
      }
      
      return novaLista;
    });
    //console.log('extrato atualizado com dia vazio: ', this.extList());
  }

  getExtList():ExtratoModel[]{
    //console.log("EXT(LIST SENDO PEGA: ", this.extList());
    return this.extList();
  }

  createMonthExt(acc: Account){ //roda por tds os dias do mÊs adicionando extratos á lista
    //console.log('ACCOUNT QUE TA NA CREATEMONTH: ', acc);
    const now = new Date();
    let month = now.getMonth() + 1;
    let days = now.getDate();

    while(month>0){
      while(days>0){
        let idString = `${now.getFullYear()}-${String(month).padStart(2, '0')}-${String(days).padStart(2, '0')}`
        let dateId = this.createDateId(idString);

        if(dateId<acc.createdAt) return;

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
      month--
      //typescript stravaganza: primeiro dia do mês[0] = ultimo dia do último mês, ou seja, qntdade de dias do mes anterior 
      days = new Date(now.getFullYear(), month, 0).getDate();
      //console.log("MONTH: ", month);
    }
    //console.log("EXTlIST: ", this.extList());
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
