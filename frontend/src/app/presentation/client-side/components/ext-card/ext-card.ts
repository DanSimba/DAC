import { Component, input } from '@angular/core';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';
import { TransferenceModel } from '../../../../domain/operations/models/transference.model';
import { OperationModel } from '../../../../domain/operations/models/operation.model';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-ext-card',
  imports: [MatIconModule],
  templateUrl: './ext-card.html',
  styleUrl: './ext-card.css',
})
export class ExtCard {
  ext = input.required<ExtratoModel>();

  //PEGA SOMENTE O OBJETO DE DENTRO DA INSTÂNCIA (COM UM ÚNICO TIPO, PRA NÃO FICAR AMBÍGUO)
  transfInstance = input<TransferenceModel>();
  operInstance = input<OperationModel>();
}
