import { Component, inject, signal } from '@angular/core';
import { Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-extrato',
  imports: [MatIconModule],
  templateUrl: './extrato.html',
  styleUrl: './extrato.css',
})
export class Extrato {
  location = inject(Location);

  date = signal<string>('2012-07-12')

  mostrarData(){
    console.log('DATA: ', this.date())
  }
}
