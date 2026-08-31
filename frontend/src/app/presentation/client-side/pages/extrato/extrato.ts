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
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
    {
      type: {
        acc_number: '011',
        side: 'dep',
        value: 10
      },
      id: 20260809
    },
    {
      type: {
        acc_number: '011',
        side: 'sac',
        value: 111
      },
      id: 20260709
    },
  ] 

  date = signal<string>('2012-07-12')

  mostrarData(){
    console.log('DATA: ', this.date())
  }
}
