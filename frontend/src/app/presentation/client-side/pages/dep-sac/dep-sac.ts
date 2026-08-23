import { Component, inject, Injectable, signal } from '@angular/core';
import { OperationModel } from '../../../../models/operation.model';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';

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

    } else {
      console.log("Operação inválida!!! ):", concreteValue)
      return
    }
  }
}
