import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ManagerDTO } from '../../domain/manager/models/managerDTO';
import { SolicitacaoCadastro } from '../../domain/manager/models/solicitacao-cadastro.model';
import { ClienteConsulta } from '../../domain/manager/models/consulta-cliente.model';
import { RecusarClientePayload } from '../../domain/manager/models/manager-requests.model';

@Injectable({
  providedIn: 'root',
})
export class ManagerHttpService {
  http = inject(HttpClient);
  private readonly api_URl = 'http://localhost:8080'; //a qualquer momento vira a oficial

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

  // Solicitação de cadastro da home do gerente
  listarSolicitacoes(): Observable<SolicitacaoCadastro[]>{
    return this.http.get<SolicitacaoCadastro[]>(`${this.api_URl}/solicitacoes`);
  }

  aprovarSolicitacao(cpf: string): Observable<void>{
    return this.http.post<void>(`${this.api_URl}/solicitacoes/${cpf}/aprovar`, null);
  }

  recusarSolicitacao(cpf: string, payload: RecusarClientePayload): Observable<void>{
    return this.http.post<void>(`${this.api_URl}/solicitacoes/${cpf}/recusar`, payload);
  }

  //buscar todos os clientes na pag de consultar clientes
  listarClientes(): Observable<ClienteConsulta[]>{
    return this.http.get<ClienteConsulta[]>(`${this.api_URl}/clientes`);
  }

}

