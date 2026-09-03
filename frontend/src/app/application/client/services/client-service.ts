import { inject, Injectable, signal } from '@angular/core';
import { Client } from '../../../domain/client/models/client.model';
import { ClientStatus } from '../../../enumeration/client-status';
import { UserType } from '../../../enumeration/user-type';
import { Account } from '../../../domain/account/models/account.model';
import { ManagerStatus } from '../../../enumeration/manager-status';
import { OperationModel } from '../../../domain/operations/models/operation.model';
import { ClientHttpService } from '../../../infraestructure/http/client.http.service';
import { Observable, tap } from 'rxjs';
import { TransferenceModel } from '../../../domain/operations/models/transference.model';
import { ExtratoModel } from '../../../domain/operations/models/extrato.model';

@Injectable({
  providedIn: 'root',
})
export class ClientService {

  private clientHttpService = inject(ClientHttpService);
  //MOCKZIN
  private client = signal<Client>({
        id       : 1,
        cpf      : '00011122233',
        name     : 'razerson nvidio da silva',
        email    : 'razer@gmail.com',
        password : 'starwars123',
        salary   : 20000,
        address  : {
          cep          : 'a',
          street      : 'a',
          number       : 'a',
          complement   : 'a',
          neighborhood : 'a',
          city         : 'a',
          uf           : 'a',
          state        : 'a',
        },
        status   : ClientStatus.ACTIVE,
        type     : UserType.CLIENT,
  });

  //CONTROLE DE LOGGADO OU NÃO
  logged = signal<boolean>(true);

  private account = signal<Account>({
      client   : this.client(),
      number   : '001',
      balance  : 1000,
      manager  : {
            id       : 2,
            name     : 'dievalson oracle pereira',
            cpf      : 'dievalson oracle pereira',
            email    : 'dievalson oracle pereira',
            password : 'dievalson oracle pereira',
            status   : ManagerStatus.ACTIVE,
            type     : UserType.MANAGER
      },
      createdAt : '10/10/2012',
  })

  //MOCK lista com tds os extratos
  private extList = signal<ExtratoModel[]> ([]);

  addExtrato(ext: OperationModel|TransferenceModel){
    //pega a data em forma de id
    const now = new Date();
    //ganbiarra pra tranformar Date no id formato AAAAMMDD
    const nowId = +`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
    //procura por esse id no extrato (se já tem o dia, coloca ext lá)
      const extDay = this.extList().find(day => day.id==nowId)
      if(extDay){
        if(ext.type == 'operation'){
          extDay.opers.push(ext);
          console.log('extrato atualizado: ', this.extList());
        } else{
          extDay.tranfs.push(ext);
          console.log('extrato atualizado: ', this.extList());
        }
        return
      }
      
    //caso não ache o dia (primeiro ext do dia), cria um novo
      const newExt: ExtratoModel = {
        tranfs: [],
        opers: [],
        id: nowId,
        saldoApos: this.account().balance //JA ESTA ATUALIZADO NA FUNÇÃO DE OPERAR()/TRANFERIR()
      }

      if(ext.type == 'operation'){
        newExt.opers.push(ext);
      } else{
        newExt.tranfs.push(ext);
      }

      this.extList.update(exts=>[newExt, ...exts]);
      console.log('extrato atualizado: ', this.extList());
  }

  getExtList():ExtratoModel[]{
    return this.extList();
  }

  getClient():Client{
    return this.client();
  }
  
  getAccount():Account{
    return this.account();
  }

  setClient(c:Client){
    this.client.set(c);
  }

  setAccount(a:Account){
    this.account.set(a);
  }

  private isMenuOpen = signal<boolean>(false);
  toggleMenu(){
    this.isMenuOpen.set(!this.isMenuOpen());
  }

  getIMO(): boolean{
    return this.isMenuOpen()
  }

  operar(op: OperationModel): Observable<Account>{
    //LÓGICA DE ADICIONAR EXTRATO, DEVE IR PARA DENTRO DO SUBSCRIBE DEPOIS
    this.addExtrato(op);

    return this.clientHttpService.operar(op).pipe(
      tap({
        next: (response)=>{
          this.account.set(response);
        },
        error: (err)=>{
          console.log('err: ', err);
        }
      })
    )
  }

  transferir(t: TransferenceModel): Observable<Account>{
    //LÓGICA DE ADICIONAR EXTRATO, DEVE IR PARA DENTRO DO SUBSCRIBE DEPOIS
    this.addExtrato(t);

    return this.clientHttpService.transferir(t).pipe(
      tap({
        next: (response)=>{
          this.account.set(response)
        },
        error: (err)=>{
          console.log('err: ', err);
        }
      })
    )
  }

  //RESOLVI FZR UMA FUNÇÃO PRA PEGAR A DATA E HORA DO JEITO QUE O RAZER GOSTA AUTOMATICAMENTE
  getCurrentTimeFormated(): string{
    const date = new Date();
    //console.log('DATA NÃO FORMATADA: ', date);

    const formatedDate = `${date.getDate()}/${date.getMonth()+1}/${date.getFullYear()} ${date.getHours()}:${date.getMinutes()}`
    //console.log('DATETIME AGR: ', formatedDate);

    return formatedDate;
  }
}
