import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environmentDev } from '../../../enviroments/enviroment.development';
import { CreateClient } from '../../domain/client/models/create-client.model';

@Injectable({
  providedIn: 'root',
})
export class ClientHttpService {
  http = inject(HttpClient);
  //private readonly API_URL = environment.apiUrl;
  private readonly API_URL = `${environmentDev.apiUrlCliente}/clientes`; // URL exposta somente para teste

  // Faz a solicitação de criação do cliente ao API Gateway
  createClientRequest(clientRequest: CreateClient): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/clientes`, clientRequest);
  }
}
