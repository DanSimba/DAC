import { Component, computed, inject, input, signal } from '@angular/core';
import { ExtratoModel } from '../../../../domain/operations/models/extrato.model';
import { TransferenceModel } from '../../../../domain/operations/models/transference.model';
import { OperationModel } from '../../../../domain/operations/models/operation.model';
import { MatIconModule } from '@angular/material/icon';
import { ClientService } from '../../../../application/client/services/client-service';
import { Client } from '../../../../domain/client/models/client.model';
import { Account } from '../../../../domain/account/models/account.model';
import { AccountService } from '../../../../application/account/services/account-service';
import { OperationSide } from '../../../../enumeration/operation-side';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-ext-card',
  imports: [MatIconModule, DecimalPipe],
  templateUrl: './ext-card.html',
  styleUrl: './ext-card.css',
})
export class ExtCard {
  clientService = inject(ClientService);
  client = signal<Client>(this.clientService.getClient());

  accountService = inject(AccountService);
  account = signal<Account>(this.accountService.getAccount());
  
  //PEGA SOMENTE O OBJETO DE DENTRO DA INSTÂNCIA (COM UM ÚNICO TIPO, PRA NÃO FICAR AMBÍGUO)
  transfInstance = input<TransferenceModel>();
  operInstance = input<OperationModel>();

  type = computed(()=>{
    console.log('OPERINSTANCE: ', this.operInstance());
    console.log('transfINSTANCE: ', this.transfInstance());
    if(this.operInstance()) return this.operInstance()?.side == OperationSide.DEP? 'dep': 'sac';
    else return 'transf'
  })

  val = computed(()=>{
    if(this.operInstance()) return String(this.operInstance()?.value).padStart(2, '0')
      else return String(this.transfInstance()?.value).padStart(2, '0')
  })
}
