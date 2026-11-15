import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environmentDev } from '../../../enviroments/enviroment.development';
import { CreateClient } from '../../domain/client/models/create-client.model';
import { Client } from '../../domain/client/models/client.model';

@Injectable({
  providedIn: 'root',
})
export class ClientHttpService {
  http = inject(HttpClient);
  //private readonly API_URL = environment.apiUrl;
  private readonly API_URL = `${environmentDev.apiUrlGateway}`;

  // Faz a solicitação de criação do cliente ao API Gateway
  createClientRequest(clientRequest: CreateClient): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/solicitacoes`, clientRequest); 
  }

  findClientByCpf(cpf: string): Observable<Client> {
    return this.http.get<Client>(`${this.API_URL}/clientes/findByCpf/${cpf}`);
  }
}
