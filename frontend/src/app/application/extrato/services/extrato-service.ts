import { Injectable, signal } from '@angular/core';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';
import { Account } from '../../../domain/account/models/account.model';

@Injectable({
  providedIn: 'root',
})
export class ExtratoService {

  private extList = signal<ExtratoModel[]> ([]);
  private realList = signal<ExtratoModel[]> ([]); //LISTA DE EXTRATOS SEM OS VAZIOS, SOMENTE OPERAÇÕES QUE REALMENTE ACONTECERAM

  //SOLUÇÃO ALTERNATIVA: PASSAR A LISTA INTEIRA DNV
  setExtList(el: ExtratoModel[]){
    this.extList.set(el);
    //console.log("EXTLIST ESTÁ ASSIM: ", this.extList())
  }

  getExtList():ExtratoModel[]{
    //console.log("EXT(LIST SENDO PEGA: ", this.extList());
    return this.extList();
  }

  setRealList(rl: ExtratoModel[]){
    this.realList.set(rl);
  }

  addToExtList(ext: ExtratoModel){
        this.extList.update(current=>[...current, ext]);
  }

  addToRealList(ext: ExtratoModel){
     this.realList.update(current=>[...current, ext]);
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
        novaLista.splice(index, 0, emptyDay);
      }
      
      return novaLista;
    });
    //console.log('extrato atualizado com dia vazio: ', this.extList());
  }


  createMonthExt(month: number, year: number, acc: Account){ //roda por tds os dias do mÊs adicionando extratos á lista
    //ARRANCA OS VAZIOS ANTERIORES E DEIXA SÓ OS EXTS DE VRDD
    this.setExtList(this.realList());

    //console.log('ACCOUNT QUE TA NA CREATEMONTH: ', acc);
    const now = new Date();

    //typescript stravaganza: primeiro dia do mês[0] = ultimo dia do último mês, ou seja, qntdade de dias do mes anterior
    let days = new Date(now.getFullYear(), month, 0).getDate();

        while(days>0){
          let idString = `${year}-${String(month).padStart(2, '0')}-${String(days).padStart(2, '0')}`
          let dateId = this.createDateId(idString);

          if(dateId<acc.createdAt) return;

          //procura se o dia ja consta na extList
          let existingDay = this.extList().find((ext)=>(ext.dateId == dateId));
          if(!existingDay){ //SE O DIA NAO EXISTE, CRIA VAZIO
            this.createEmptyDay(dateId, acc);
          }
          days--
        } 
    console.log("EXTlIST: ", this.extList());
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
