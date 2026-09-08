import { Injectable, signal } from '@angular/core';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';
import { OperationModel } from '../../../domain/operations/models/operation.model';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';

@Injectable({
  providedIn: 'root',
})
export class ExtratoService {

  private extList = signal<ExtratoModel[]> ([]);

  createExtrato(ext: OperationModel|TransferenceModel|null, id: number, saldoApos:number){ //null para popular a lista com dias vazios

      //procura por esse id no extrato (se já tem o dia, coloca ext lá)
        const extDay = this.extList().find(day => day.id==id)
        if(extDay){
          if(ext){
            if(ext.type == 'operation'){
                extDay.opers.push(ext);
                console.log('extrato atualizado: ', this.extList());
            } else{
                extDay.tranfs.push(ext);
                console.log('extrato atualizado: ', this.extList());
            }
            return
          } else return //SE EXISTE ALGO NO DIA, ENTÃO NÃO PRECISA ADICIONAR DIA VAZIO NO LUGAR
        }
        
      //caso não ache o dia (primeiro ext do dia), cria um novo
        const newExt: ExtratoModel = {
          tranfs: [],
          opers: [],
          id: id,
          saldoApos: saldoApos //JA ESTA ATUALIZADO NA FUNÇÃO DE OPERAR()/TRANFERIR()
        }
  
        if(ext){
          if(ext.type == 'operation'){
            newExt.opers.push(ext);
          } else{
            newExt.tranfs.push(ext);
          }
        }
  
        this.extList.update(exts=>[newExt, ...exts]);
        console.log('extrato atualizado: ', this.extList());
  }
  
  getExtList():ExtratoModel[]{
    return this.extList();
  }

  createMonthExt(saldo: number){ //roda por tds os dias do mÊs adicionando extratos á lista
    const now = new Date();
    let days = now.getDate();

    while(days>0){
      let idString = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(days).padStart(2, '0')}`
      let id = this.createDateId(idString);

      //miseria.ts
      //encontra a ultima oper / transf na lista antes do dia vazio
      let lastOp = this.extList().find((ext)=>(ext.opers.length>0||ext.tranfs.length>0)&&ext.id<id);
      if(lastOp){
         this.createExtrato(null, id, lastOp.saldoApos);
      }else{
        this.createExtrato(null, id, saldo);
      }
      days--
    }
  }

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
