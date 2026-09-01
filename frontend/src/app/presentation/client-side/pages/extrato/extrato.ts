import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';
import { ExtCard } from '../../components/ext-card/ext-card';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';

@Component({
  selector: 'app-extrato',
  imports: [MatIconModule, ExtCard],
  templateUrl: './extrato.html',
  styleUrl: './extrato.css',
})
export class Extrato implements OnInit{
  location = inject(Location);
  clientService = inject(ClientService);
  client = signal<Client>(this.clientService.getClient());
  account = signal<Account>(this.clientService.getAccount());

  //MOCK lista com tds os extratos
  extList = signal<ExtratoModel[]> ([
    { 
      type: 'transf',
      instance:{
        type: 'transference',
        cpf_origin: '11122233355',
        name_origin: 'pedro torresmos',
        acc_origin: '003',
        
        acc_destiny: this.account().number,

        value: 1000,
        datetime: '10/10/2010 18:32'
      },
      id: 20101010,
    },
    { 
      type: 'dep',
      instance:{
        type: 'operation',
        acc_number: this.account().number,
        side: 'dep',
        value: 300,
        datetime: '01/01/2020 16:02'
      },
      id: 20200101,
    },
    { 
      type: 'sac',
      instance:{
        type: 'operation',
        acc_number:  this.account().number,
        side: 'sac',
        value: 300,
        datetime:'19/01/2021 12:33'
      },
      id: 20210119,
    },
  ]);

  //filtra o extrato pela data
  filteredList= computed(()=>{
    return this.extList().filter((ext)=> ext.id<=this.date())
  })

  date = signal<number>(20260831)

  ngOnInit(): void {
    const now = new Date();
    //ganbiarra pra tranformar Date no formato que o input aceita
    const nowString = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${now.getDate()}`
    //console.log("now: ", nowString)

    this.setDate(nowString);
  }

  setDate(d:string){ //d = 'aaaa-mm-dd'

    //pega a string e tira os traços
    const dateInt = d.split("-");
    //console.log('dateInt: ', dateInt);

    this.date.set(+(dateInt[0]+dateInt[1]+dateInt[2]));
    console.log('date: ', this.date());
  }
  

  mostrarData(){
    console.log('DATA: ', this.date())
  }
}
