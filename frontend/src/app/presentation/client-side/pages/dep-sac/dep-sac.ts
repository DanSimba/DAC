import { Component, inject, signal } from '@angular/core';
import { OperationModel } from '../../../../domain/operations/models/operation.model';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PopUp } from '../../../shared/components/pop-up/pop-up';
import { ClientService } from '../../../../application/client/services/client-service';

@Component({
  selector: 'app-dep-sac',
  imports: [MatIconModule],
  templateUrl: './dep-sac.html',
  styleUrl: './dep-sac.css',
})
export class DepSac {
  location = inject(Location);

  clientService = inject(ClientService);

  side = signal<string>('');
  value = signal<number>(0);

  setSide(s: string){
    this.side.set(s);
  }

  send(){
    const concreteSide = this.side();
    const concreteValue = this.value();

    if(concreteSide!='' && concreteValue>0){
        const op: OperationModel = {
          type:'operation',
          acc_number: this.clientService.getAccount().number,
          side:concreteSide,
          value: concreteValue,
          datetime: this.clientService.getCurrentTimeFormated()
        }

        this.clientService.operar(op).subscribe({
          //VALUE É A CONTA COM O SALDO ATUALIZADO
          next: (value) => {
            //JÁ SETA A CONTA COM O RETORNO LÁ NO SERVICE
            this.showPopUp('Operação efetuada com sucesso!!!', 'success');
          },
          error: (err)=> {
            this.showPopUp('Erro ao efetuar operação!!!', 'fail');
            console.log('ERR: ', err);
          },
        });

        //console.log('OPERAÇÃO: ', op);
    } else {
      //console.log("Operação inválida!!! ):", concreteValue);
      this.showPopUp('Operação inválida!!!', 'fail');
      return
    }
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
