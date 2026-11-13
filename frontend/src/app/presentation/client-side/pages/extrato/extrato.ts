import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';
import { ExtCard } from '../../components/ext-card/ext-card';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { ExtratoService } from '../../../../application/extrato/services/extrato-service';
import { AccountService } from '../../../../application/account/services/account-service';
import { response } from 'express';

@Component({
  selector: 'app-extrato',
  imports: [MatIconModule, ExtCard],
  templateUrl: './extrato.html',
  styleUrl: './extrato.css',
})
export class Extrato implements OnInit, OnDestroy{
  location = inject(Location);

  accountService = inject(AccountService);
  account = computed(():Account =>{
    return this.accountService.getAccount();
  })

  clientService = inject(ClientService);
  extratoService = inject(ExtratoService);

  client = signal<Client>(this.clientService.getClient());

  endDate = signal<number>(0);
  startDate = signal<number>(0);
  minDate = signal<string>('');
  maxDate = signal<string>('');

  //MOCK lista com tds os extratos
  extList = computed(():ExtratoModel[]=>{
    return this.extratoService.getExtList();
  })

  //filtra o extrato pela data
  filteredList= computed(()=>{
    return this.extList().filter((ext)=> ext.dateId<=this.endDate()&&ext.dateId>=this.startDate()&&ext.dateId>=this.account().createdAt).reverse();
  })

  ngOnInit(): void {

        this.accountService.listExtratos().subscribe({
          next: (response)=>{
            //console.log("CONTA PUXADA: ", response);
            //console.log("LISTA QUE TA NO COMP: ", this.extList())
        
            const now = new Date();
            //ganbiarra pra tranformar Date no formato que o input aceita
            const nowString = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`

            //MINIMO DE TEMPO
            const minString = `${now.getFullYear()-1}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
            console.log("now: ", nowString)
            //console.log("MIN: ", minString)
            this.minDate.set(minString);
            this.maxDate.set(nowString); //não pode olhar além de hoje
            this.setDate(nowString);
            //popula ultimos 30
            this.extratoService.createMonthExt(now.getMonth() + 1, now.getFullYear(), response);
          }
        })
  }

  ngOnDestroy(): void {
    this.extratoService.clearMonthExt();
  }

  setDate(d:string){ //d = 'aaaa-mm-dd'
    //pega a string e tira os traços
    const dateInt = d.split("-");
    //console.log('dateInt: ', dateInt);

    //seta a data
    this.endDate.set(+(dateInt[0]+dateInt[1]+dateInt[2]));
    //console.log('enddate: ', this.endDate());

    //seta o começo um mes antes do fim
    this.startDate.set(+(dateInt[0]+(+dateInt[1]-1)+'01'));
    //console.log('startdate: ', this.startDate());

    //popula mes atual e o anterior que vai aparecer
    this.extratoService.createMonthExt(+dateInt[1], +dateInt[0], this.account());
    this.extratoService.createMonthExt((+dateInt[1]-1), +dateInt[0], this.account());

  }

  setEndDate(d:string){ //d = 'aaaa-mm-dd'
    const dateInt = d.split("-");
    this.endDate.set(+(dateInt[0]+dateInt[1]+dateInt[2]));
  }

  setStartDate(d:string){ //d = 'aaaa-mm-dd'
    const dateInt = d.split("-");
    this.startDate.set(+(dateInt[0]+dateInt[1]+dateInt[2]));

    //CASO O USUÁRIO ESCOLHA MOSTRAR MAIS DE UM MES, POPULA A LISTA COM TDS OS MESES QUE ENTRAM
    this.extratoService.createMonthExt(+dateInt[1], +dateInt[0], this.account())
  }
  
  formatDateId(d:number):string{
    const dString = d.toString();
    //console.log('DSTRING: ', dString)
    const formatedDate = `${dString.slice(6,8)}/${dString.slice(4,6)}/${dString.slice(0,4)}`
    return formatedDate;
  }

  stringfyDateId(d:number):string{
    const dString = d.toString();
    //console.log('DSTRING: ', dString)
    const formatedDate = `${dString.slice(0,4)}-${dString.slice(4,6)}-${dString.slice(6,8)}`
    return formatedDate;
  }
}
