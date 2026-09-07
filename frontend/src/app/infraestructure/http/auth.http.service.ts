import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest } from '../../domain/auth/models/login-request.model';
import { AuthResponse } from '../../domain/auth/models/auth-response.model';

@Injectable({
  providedIn: 'root',
})

export class AuthHttpService {
  // Injeta o HttpClient para fazer requisições HTTP
  private http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:3000';

  // Faz o POST de login enviadno as credenciais e retorna a resposta de autenticação
  public login(credentials: LoginRequest): Observable<AuthResponse> {
    // Pega a resposta e mapeia para o model AuthResponse
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials);
  }
}