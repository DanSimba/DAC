import { Component, inject, signal, computed } from '@angular/core';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PopUp } from '../../../shared/components/pop-up/pop-up';
import { TransferenceModel } from '../../../../domain/operations/models/transference.model';

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

    if(concreteValue>this.account().balance){
      this.showPopUp('Saldo insuficiente!!!', 'fail')
      return
    }

    //DEU CERTO
      const concreteTransference: TransferenceModel ={
        type:'transference',
        cpf_origin: this.client().cpf,
        name_origin: this.client().name,
        acc_origin: this.account().number,
        
        acc_destiny: concreteDest.toString(),

        value: concreteValue,
        datetime: this.clientService.getCurrentTimeFormated()
      }

      this.clientService.transferir(concreteTransference).subscribe({
        next: (response) => {
          //JÁ SETA A CONTA ATUALIZADA COM O RETORNO LÁ NO SERVICE

          //ATUALIZA O SALDO NA TELA
          this.account.set(response);

          //ANIMAÇÃO MTO FODA E LIMMPA INPUTS
          this.sent.set(true);

          this.dest.set(null);
          this.value.set(null);

          setTimeout(() => {
            this.sent.set(false);
          }, 3000);
        }, 
        error: (err) => {
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
