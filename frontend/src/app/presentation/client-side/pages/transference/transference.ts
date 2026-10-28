import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PopUp } from '../../../shared/components/pop-up/pop-up';
import { TransferenceModel } from '../../../../domain/operations/models/transference.model';
import { AccountService } from '../../../../application/account/services/account-service';

@Component({
  selector: 'app-transference',
  imports: [MatIconModule],
  templateUrl: './transference.html',
  styleUrl: './transference.css',
})
export class Transference{
  location = inject(Location);

  clientService = inject(ClientService);
  client = signal<Client>(this.clientService.getClient());
  
  accountService = inject(AccountService);
  account = computed((): Account=>{
    return this.accountService.getAccount();
  })

  dest = signal<string>('');
  value = signal<number|null>(null);

  isComplete = computed(()=>{
    return this.dest()!=null && this.value()!=null;
  })

  sent = signal<boolean>(false);

  send(){
    //VERIFICAÇÕES
    const concreteDest = this.dest()
    const concreteValue = this.value();
     if(!concreteValue||!concreteDest){
      this.showPopUp('Preencha todos os campos!!!', 'fail')
      return
     } 

    if(concreteValue>this.account().balanco){
      this.showPopUp('Saldo insuficiente!!!', 'fail')
      return
    }

    //DEU CERTO
      const concreteTransference: TransferenceModel ={
        type:'transference',
        cpfOrigin: this.client().cpf,
        nameOrigin: this.client().name,
        nameDestiny: '', //VAI PARA O MS-ACCOUNT VAZIO, O GATEWAY QUE PROCURA ISSO AQ
        accOrigin: this.account().number,
        
        accDestiny: concreteDest.toString(),

        value: concreteValue,
        datetime: this.accountService.getCurrentTimeFormated()
      }

      console.log("SALDO NA HORA DA TRANSF: ", this.account().balanco);

      console.log("CONCRETETRANSF: ", concreteTransference);

      this.accountService.transferir(concreteTransference).subscribe({
        next: (response) => {
          //JÁ SETA A CONTA ATUALIZADA COM O RETORNO LÁ NO SERVICE, POR ISSO N PRECISA RETORNAR ND AQ

          //ANIMAÇÃO MTO FODA E LIMMPA INPUTS

          console.log("RESPONSE: ", response);
          this.sent.set(true);

          setTimeout(() => {
            this.dest.set('');
            this.value.set(null);
            this.sent.set(false);
          }, 3000);
        }, 
        error: (err) => {
          console.log("ERR:: ", err)
          this.showPopUp('Destinatário não encontrado!!!', 'fail');
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
