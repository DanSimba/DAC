import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-autocadastro',
  imports: [FormsModule],
  templateUrl: './autocadastro.html',
  styleUrl: './autocadastro.css',
})
export class Autocadastro{

  public nome: string = '';
  public cpf: string = '';
  public email: string = '';
  public telefone: string = '';
  public salario: number | null = null;
  
  public cep: string = '';
  public logradouro: string = '';
  public numero: string = '';
  public complemento: string = '';
  public cidade: string = '';
  public estado: string = '';

  public mensagemSucesso: string = '';
  public mensagemErro: string = '';

  public onSubmit(): void {
    if (!this.nome || !this.cpf || !this.email || this.salario === null) {
      this.mensagemErro = 'Por favor, preencha os campos obrigatórios (*).';
      this.mensagemSucesso = '';
      return;
    }

    if (this.salario <= 0) {
      this.mensagemErro = 'O salário deve ser um valor positivo.';
      this.mensagemSucesso = '';
      return;
    }

    this.mensagemErro = '';
    this.mensagemSucesso = 'Solicitação de autocadastro enviada com sucesso! Aguarde a análise do gerente.';
    
  }
  
}