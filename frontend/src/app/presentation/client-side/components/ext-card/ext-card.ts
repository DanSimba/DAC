import { Component, input } from '@angular/core';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';

@Component({
  selector: 'app-ext-card',
  imports: [],
  templateUrl: './ext-card.html',
  styleUrl: './ext-card.css',
})
export class ExtCard {
  ext = input.required<ExtratoModel>();
}
