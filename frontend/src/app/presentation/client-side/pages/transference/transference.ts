import { Component, inject, signal } from '@angular/core';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../models/client.model';
import { Account } from '../../../../models/account.model';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PopUp } from '../../../shared/components/pop-up/pop-up';
import { TransferenceModel } from '../../../../models/transference.model';
import { response } from 'express';

@Component({
  selector: 'app-transference',
  imports: [MatIconModule],
  templateUrl: './transference.html',
  styleUrl: './transference.css',
})
export class Transference {
  location = inject(Location);

  clientService = inject(ClientService);
  client = signal<Client>(this.clientService.getClient());
  account = signal<Account>(this.clientService.getAccount());

  dest = signal<number|null>(null);
  
  value = signal<number|null>(null);

  send(){
    //VERIFICAÇÕES
    const concreteDest = this.dest()
    const concreteValue = this.value();
     if(!concreteValue||!concreteDest){
      this.showPopUp('Preencha todos os campos!!!', 'fail')
      return
     } 

    if(concreteValue>this.account().balance){
      this.showPopUp('Saldo insuficiente!!!', 'fail')
      return
    }

    const concreteTransference: TransferenceModel ={
      cpf_origin: this.client().cpf,
      name_origin: this.client().name,
      acc_origin: this.account().number,
      
      acc_destiny: concreteDest.toString(),

      value: concreteValue,
      datetime: this.clientService.getCurrentTimeFormated()
    }

    this.clientService.transferir(concreteTransference).subscribe({
      next: (response) => {
        //JÁ ATUALIZA O SALDO NA TELA
        this.account.set(response);

        //ATUALIZA NO CACHE
        this.clientService.setAccount(response);
      }, 
      error: (err) => {
        this.showPopUp('Destinatário não encontrado!!!', 'fail');
        console.log('ERR: ', err);
        return
      }
    })

  }

  //POPUP
  private popUp = inject(MatSnackBar);

  showPopUp(msg:string, style:string) {
    this.popUp.openFromComponent(PopUp, {
      duration: 4000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['none'], // Used to strip default Material backgrounds
      data: { message: msg, style: style }
    });
  }
}
