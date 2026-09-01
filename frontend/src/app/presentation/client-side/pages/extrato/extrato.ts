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
  extList = signal<ExtratoModel[]> (this.clientService.getExtList());

  //filtra o extrato pela data
  filteredList= computed(()=>{
    return this.extList().filter((ext)=> ext.id<=this.date())
  })

  date = signal<number>(20260831)

  ngOnInit(): void {
    const now = new Date();
    //ganbiarra pra tranformar Date no formato que o input aceita
    const nowString = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
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
