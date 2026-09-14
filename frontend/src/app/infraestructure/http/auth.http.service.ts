import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginRequest } from '../../domain/auth/models/login-request.model';
import { AuthResponse } from '../../domain/auth/models/auth-response.model';

@Injectable({
  providedIn: 'root',
})

export class AuthHttpService {

  // Injeta o HttpClient para fazer requisições HTTP 
  private http = inject(HttpClient);
  private router = inject(Router);

  // o front deve se comunicar com o back só pela pelo apigeteway na porta 3000
  private readonly API_URL = 'http://localhost:3000';

  // Faz o POST de login enviadno as credenciais e retorna a resposta de autenticação
  public login(credentials: LoginRequest): Observable<AuthResponse> {
    // Pega a resposta e mapeia para o model AuthResponse
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials);
  }

  //RF02 
  public logout(): void {
    // Limpa o localStorage removendo os dados da sessão e redireciona para a página de login
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    localStorage.removeItem('tipo');
    this.router.navigate(['/']); // vai para a tela de deslogado
  }

  // metodo pra checar se o usuario está logado
  public islogged(): boolean {
    //verifica se o token ta no localStorage para determinar se o usuário está logado
    const token = localStorage.getItem('token');
    return !!token;
  }

}