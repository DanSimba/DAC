import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';
import { ExtCard } from '../../components/ext-card/ext-card';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { ExtratoService } from '../../../../application/extrato/services/extrato-service';
import { AccountService } from '../../../../application/account/services/account-service';

@Component({
  selector: 'app-extrato',
  imports: [MatIconModule, ExtCard],
  templateUrl: './extrato.html',
  styleUrl: './extrato.css',
})
export class Extrato implements OnInit{
  location = inject(Location);

  accountService = inject(AccountService);
  account = signal<Account>(this.accountService.getAccount());

  clientService = inject(ClientService);
  extratoService = inject(ExtratoService);

  client = signal<Client>(this.clientService.getClient());

  date = signal<number>(0);
  fotm = signal<number>(0); //FIRST OF THE MONTH
  minDate = signal<string>('');

  //MOCK lista com tds os extratos
  extList = computed(():ExtratoModel[]=>{
    return this.extratoService.getExtList();
  })
  //filtra o extrato pela data
  filteredList= computed(()=>{
    return this.extList().filter((ext)=> ext.dateId<=this.date()&&ext.dateId>=this.fotm()).reverse();
  })

  ngOnInit(): void {

    //PRIMEIRO FAZ UM GETEXTRATOLIST
    //this.accountService.listExtratos();
    //this.extList.set(this.extratoService.getExtList());

    //console.log("LISTA QUE TA NO COMP: ", this.extList())
    
    const now = new Date();
    //ganbiarra pra tranformar Date no formato que o input aceita
    const nowString = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`

    //MINIMO DE TEMPO
    const minString = `${now.getFullYear()-1}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    //console.log("now: ", nowString)
    //console.log("MIN: ", minString)
    this.minDate.set(minString);
    this.setDate(nowString);

    //popula mes atual
    this.extratoService.createMonthExt(now.getMonth() + 1, now.getFullYear(), this.account());
  }

  setDate(d:string){ //d = 'aaaa-mm-dd'
    //pega a string e tira os traços
    const dateInt = d.split("-");
    //console.log('dateInt: ', dateInt);

    //seta a data
    this.date.set(+(dateInt[0]+dateInt[1]+dateInt[2]));
    console.log('date: ', this.date());

    //seta o começo do mes junto
    this.fotm.set(+(dateInt[0]+dateInt[1]+'01'));

    //popula mes escolhido
    this.extratoService.createMonthExt(+dateInt[1], +dateInt[0], this.account());

  }
  
  //FAZ LITERALMENTE O CONTRARIO DA FUNÇÃO DE CIMA 
  formatDateId(d:number):string{
    const dString = d.toString();
    //console.log('DSTRING: ', dString)
    const formatedDate = `${dString.slice(6,8)}/${dString.slice(4,6)}/${dString.slice(0,4)}`
    return formatedDate;
  }
}
