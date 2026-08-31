import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {

public email: string = '';
  public password: string = '';
  public mostrarSenha: boolean = false;

  public mensagemErro: string = '';
  public mensagemSucesso: string = '';

  public alternarMostrarSenha(): void {
    this.mostrarSenha = !this.mostrarSenha;
  }

  public onSubmit(): void {
    if (!this.email || !this.password) {
      this.mensagemErro = 'Por favor, preencha o e-mail e a senha.';
      this.mensagemSucesso = '';
      return;
    }

    this.mensagemErro = '';
    this.mensagemSucesso = 'Login efetuado com sucesso.';
  }

}
