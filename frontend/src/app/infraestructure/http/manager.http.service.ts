import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ManagerDTO } from '../../domain/manager/models/managerDTO';
import { SolicitacaoCadastro } from '../../domain/manager/models/solicitacao-cadastro.model';
import { ClienteConsulta } from '../../domain/manager/models/consulta-cliente.model';
import { RecusarClientePayload } from '../../domain/manager/models/manager-requests.model';
import { RelatorioCliente } from '../../domain/manager/models/relatorio-cliente.model';
import { JobPollingService } from './JobPolling.service';

@Injectable({
  providedIn: 'root',
})
export class ManagerHttpService {
  private http = inject(HttpClient);
  private jobPolling = inject(JobPollingService);
  private readonly api_URl = 'http://localhost:8080'; //a qualquer momento vira a oficial

  listarGerentes(): Observable<ManagerDTO[]>{
    return this.http.get<{ gerentes: ManagerDTO[] }>(`${this.api_URl}/gerentes`).pipe(
      map(resposta => resposta.gerentes)
    );
  }

  buscarGerente(id:number): Observable<ManagerDTO>{
    return this.http.get<ManagerDTO>(`${this.api_URl}/gerentes/${id}`);
  }

  criarGerente(gerente: ManagerDTO): Observable<ManagerDTO> {
    const req$ = this.http.post<any>(
      `${this.api_URl}/gerentes`,
      gerente,
      { observe: 'response' }
    );
    return this.jobPolling.executarComPolling<ManagerDTO>(req$, true);
  }

  deletarGerente(id: number): Observable<void> {
    const req$ = this.http.delete<any>(
      `${this.api_URl}/gerentes/${id}`,
      { observe: 'response' }
    );
    return this.jobPolling.executarComPolling<void>(req$, true);
  }

  atualizarGerente(id: number, gerente:ManagerDTO): Observable<ManagerDTO>{
    return this.http.put<ManagerDTO>(`${this.api_URl}/gerentes/${id}`, {
      nome: gerente.nome,
      telefone: gerente.telefone
    });
  }

  // Solicitação de cadastro da home do gerente
  listarSolicitacoes(): Observable<SolicitacaoCadastro[]>{
    return this.http.get<{ solicitacoes: SolicitacaoCadastro[] }>(`${this.api_URl}/solicitacoes`).pipe(
      map(resposta => resposta.solicitacoes)
    );

  }

  // aprovarSolicitacao(cpf: string): Observable<void>{
  //   return this.http.post<void>(`${this.api_URl}/solicitacoes/${cpf}/aprovacao`, null);
  // }

  aprovarSolicitacao(cpf: string): Observable<any> {
    const req$ = this.http.post<any>(
      `${this.api_URl}/solicitacoes/${cpf}/aprovacao`,
      null,
      { observe: 'response' }
    );
    return this.jobPolling.executarComPolling<any>(req$, true);
  }

  recusarSolicitacao(cpf: string, payload: RecusarClientePayload): Observable<void>{
    return this.http.post<void>(`${this.api_URl}/solicitacoes/${cpf}/rejeicao`, payload);
  }

  //buscar todos os clientes na pag de consultar clientes
  listarClientes(): Observable<ClienteConsulta[]>{
    return this.http.get<{ clientes: ClienteConsulta[] }>(`${this.api_URl}/clientes`).pipe(
      map(resposta => resposta.clientes)
    );
  }

  listarRelatorioClientes(): Observable<RelatorioCliente[]> {
    const req$ = this.http.get<any>(
      `${this.api_URl}/relatorios/clientes`,
      { observe: 'response' }
    );
    return this.jobPolling.executarComPolling<{ clientes: RelatorioCliente[] }>(req$, true).pipe(
      map(resposta => resposta.clientes)
    );
  }

}

