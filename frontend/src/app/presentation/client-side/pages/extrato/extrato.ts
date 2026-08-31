import { Component, inject, signal } from '@angular/core';
import { Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';
import { ExtCard } from '../../components/ext-card/ext-card';

@Component({
  selector: 'app-extrato',
  imports: [MatIconModule, ExtCard],
  templateUrl: './extrato.html',
  styleUrl: './extrato.css',
})
export class Extrato {
  location = inject(Location);

  extList: ExtratoModel[] = [
    { 
      type:'dep',
      id: 20260723
    },
    { 
      type:'sac',
      id: 20260723
    },
    { 
      type:'transf',
      id: 20260723
    },
    { 
      type:'dep',
      id: 20260723
    },
  ] 

  date = signal<string>('2012-07-12')

  mostrarData(){
    console.log('DATA: ', this.date())
  }
}
