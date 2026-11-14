import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../../domain/auth/models/login-request.model';
import { AuthResponse, UsuarioAutenticado } from '../../domain/auth/models/auth-response.model';
import { UserType } from '../../enumeration/user-type';

@Injectable({
  providedIn: 'root',
})

export class AuthHttpService {

  // Injeta o HttpClient para fazer requisições HTTP 
  private http = inject(HttpClient);
  private router = inject(Router);

  // o front deve se comunicar com o back só pela pelo apigeteway na porta 3000
  private readonly API_URL = 'http://localhost:8080';

  // Faz o POST de login enviadno as credenciais e retorna a resposta de autenticação
  public login(credentials: LoginRequest): Observable<AuthResponse> {
    // Pega a resposta e mapeia para o model AuthResponse
    const body = {
      email: credentials.login,
      senha: credentials.senha
    };
    return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, body).pipe(
      tap((response) => {
        if (response.auth && response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('tipo', String(response.tipo));
          localStorage.setItem('usuario', JSON.stringify(response.usuario));
        }
      })
    );
  }

  //RF02 
  public logout(): void {
    this.http.post(`${this.API_URL}/auth/logout`, {}).subscribe({
      next: () => this.limparSessaoLocal(),
      error: () => this.limparSessaoLocal()
    })
  }

  public limparSessaoLocal(): void {
    // Limpa o localStorage removendo os dados da sessão e redireciona para a página de login
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    localStorage.removeItem('tipo');
    this.router.navigate(['/']); // vai para a tela de deslogado
  }

  public getToken(): string | null {
    return localStorage.getItem('token');
  }

  public getTipo(): UserType | string | null {
    return localStorage.getItem('tipo');
  }

  public getUsuario(): UsuarioAutenticado | null {
    const raw = localStorage.getItem('usuario');
    return raw ? (JSON.parse(raw) as UsuarioAutenticado) : null;
  }

  // metodo pra checar se o usuario está logado
  public islogged(): boolean {
    //verifica se o token ta no localStorage para determinar se o usuário está logado
    // const token = localStorage.getItem('token');
    // return !!token;
    const token = this.getToken();
    if (!token) return false;

    try {
      const payloadBase64 = token.split('.')[1];
      const payload = JSON.parse(atob(payloadBase64));
      const agoraEmSegundos = Math.floor(Date.now() / 1000);

      if (payload.exp && payload.exp < agoraEmSegundos) {
        this.limparSessaoLocal();
        return false;
      }
      return true;
    } catch (error) {
      this.limparSessaoLocal();
      return false;
    }
  }

}