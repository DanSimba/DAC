import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ManagerDTO } from '../../domain/manager/models/managerDTO';

@Injectable({
  providedIn: 'root',
})
export class ManagerHttpService {
  http = inject(HttpClient);
  private readonly api_URl = 'http://localhost/8080'; //a qualquer momento vira a oficial

  listarGerentes(): Observable<ManagerDTO[]>{
    return this.http.get<ManagerDTO[]>(`${this.api_URl}/gerentes`);
  }

  buscarGerente(id:number): Observable<ManagerDTO>{
    return this.http.get<ManagerDTO>(`${this.api_URl}/gerentes/${id}`);
  }

  criarGerente(gerente:ManagerDTO): Observable<ManagerDTO>{
    return this.http.post<ManagerDTO>(`${this.api_URl}/gerentes`, gerente);
  }

  deletarGerente(id:number): Observable<void>{
    return this.http.delete<void>(`${this.api_URl}/gerentes/${id}`);
  }

  atualizarGerente(id: number, gerente:ManagerDTO): Observable<ManagerDTO>{
    return this.http.put<ManagerDTO>(`${this.api_URl}/gerentes/${id}`, gerente);
  }

}

