import { inject, Injectable, signal } from '@angular/core';
import { ManagerHttpService } from '../../../infraestructure/http/manager.http.service';
import { ManagerDTO } from '../../../domain/manager/models/managerDTO';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ManagerService {

  private isMenuOpen = signal<boolean>(false);
  toggleMenu(){
    this.isMenuOpen.set(!this.isMenuOpen());
  }
  getIMO(): boolean{
    return this.isMenuOpen()
  }

  private managerHttpService = inject(ManagerHttpService);
  private managers = signal<ManagerDTO[]>([]);
  getManagers(): ManagerDTO[]{
    return this.managers();
  }

  carregarGerente(): Observable<ManagerDTO[]>{
    return this.managerHttpService.listarGerentes().pipe(
      tap({
        next: (lista) => this.managers.set(lista),
        error: (err) => console.log('não rolou a listagem fio', err),
      })
    )
  }

  criarGerente(gerente: ManagerDTO): Observable<ManagerDTO>{
    return this.managerHttpService.criarGerente(gerente).pipe(
      tap({
        next: (criado) => this.managers.update((lista) => [...lista, criado]),
        error: (err) => console.log('alguém não foi criado', err),
      })
    )
  }

  atualizarGerente(id:number, gerente:ManagerDTO): Observable<ManagerDTO>{
    return this.managerHttpService.atualizarGerente(id, gerente).pipe(
      tap({
        next: (atual) => this.managers.update((lista)=>
        lista.map((m)=>(m.id === id ? atual: m))),
        error: (err) => console.log('alguém não foi atualizado', err),
      })
    )
  }

  removerGerente(id:number): Observable<void>{
    return this.managerHttpService.deletarGerente(id).pipe(
      tap({
        next: () => this.managers.update((lista) => lista.filter((m)=> m.id !== id)),
        error: (err) => console.log('não deletou anda filhão', err),
      })
    )

  }


}
