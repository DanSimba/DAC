import { Component, inject, Injectable, signal } from '@angular/core';
import { OperationModel } from '../../../../models/operation.model';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PopUp } from '../../../shared/components/pop-up/pop-up';
import { fail } from 'assert';

@Component({
  selector: 'app-dep-sac',
  imports: [MatIconModule],
  templateUrl: './dep-sac.html',
  styleUrl: './dep-sac.css',
})
export class DepSac {
  location = inject(Location);

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
        side:concreteSide,
        value: concreteValue
      }

      console.log('OERAÇÃO: ', op);
      this.showPopUp('Operação efetuada com sucesso!!!', 'success');

    } else {
      console.log("Operação inválida!!! ):", concreteValue);
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
