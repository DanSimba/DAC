import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthHttpService } from '../../../../infraestructure/http/auth.http.service';
import { LoginRequest } from '../../../../domain/auth/models/login-request.model';
import { AuthResponse } from '../../../../domain/auth/models/auth-response.model';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {
  private authService = inject(AuthHttpService);
  private router = inject(Router);

  public credentials: LoginRequest = {
    login: '',
    senha: '',
  };

  public mostrarSenha: boolean = false;
  public mensagemErro: string = '';
  public mensagemSucesso: string = '';

  public alternarMostrarSenha(): void {
    this.mostrarSenha = !this.mostrarSenha;
  }

  public onSubmit(): void {
    if (!this.credentials.login || !this.credentials.senha) {
      this.mensagemErro = 'Por favor, preencha o e-mail e a senha.';
      this.mensagemSucesso = '';
      return;
    }


    this.mensagemErro = '';
    this.mensagemSucesso = 'Carregando...';
    
    // Faz a solicitação de login ao back-end usando o AuthHttpService
    this.authService.login(this.credentials).subscribe({
      next: (response: AuthResponse) => {
        this.mensagemSucesso = 'Login realizado com sucesso! Redirecionando...';
        this.mensagemErro = '';

        // Salva o token JWT e as informações da sessão no localStorage
        localStorage.setItem('token', response.token); // Salva o token JWT
        localStorage.setItem('usuario', JSON.stringify(response.usuario)); // Salva as informações do usuário autenticado
        localStorage.setItem('tipo', String(response.tipo)); // Salva o tipo de usuário

        // Redireciona para o painel de acordo com o tipo de usuário retornado
        if (response.tipo === 'MANAGER') {
          this.router.navigate(['/manager']);
        } else {
          this.router.navigate(['/client']);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.mensagemSucesso = '';

        // Se retornar 401 as credenciais estão erradas
        if (error.status === 401) {
          this.mensagemErro = error.error?.message || 'Login inválido!';
          // Se retornar 0 o back não está respondendo 
        } else if (error.status === 0) {
          this.mensagemErro = 'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro = 'Ocorreu um erro ao tentar efetuar o login. Tente novamente.';
        }
      },
      
    });
  }

}
