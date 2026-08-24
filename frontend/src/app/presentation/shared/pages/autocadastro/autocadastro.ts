import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Viacep } from '../../../../services/viacep';
import { HttpErrorResponse } from '@angular/common/http';
import { ViacepResponse } from '../../../../models/viacep-response.model';
import { Address } from '../../../../models/address.model';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-autocadastro',
  imports: [FormsModule, NgxMaskDirective],
  templateUrl: './autocadastro.html',
  styleUrl: './autocadastro.css',
})
export class Autocadastro {

  public nome: string = '';
  public cpf: string = '';
  public email: string = '';
  public telefone: string = '';
  public salario: number | null = null;
  
  public address : Address = {
    cep: '',
    street: '',
    number: '',
    complement: '',
    neighborhood: '',
    city: '',
    uf: '',
    state: ''
  };
  
  /* trecho comentado para utilizar as models existentes. Recomenda-se fazer o mesmo com os campos do usuário
    public cep: string = '';
    public logradouro: string = '';
    public numero: string = '';
    public complemento: string = '';
    public bairro: string = '';
    public cidade: string = '';
    public uf: string = '';
    public estado: string = '';
  */

  public mensagemSucesso: string = '';
  public mensagemErro: string = '';

  constructor(private viaCepService : Viacep) {}

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

  // Valida CEP antes da consulta na API ViaCEP
  verifyCep(cep: string): void {
    const cleanCep = cep.replace(/\D/g, '');

    if(cleanCep === '') {
      // Não mostra nada na tela para não atrapalhar a digitação
      return;
    } else if(cleanCep.length < 8) {
      this.clearAddressFromCep();
      return;
    } else if(cleanCep.length > 8) {
      // Somente mostra o erro quando exceder os caracteres
      // Corrigir quando for adicionada ngMask
      this.mensagemErro = 'O campo CEP deve conter 8 caracteres.';
      this.mensagemSucesso = '';
      return;
    } else {
      this.searchCep(cleanCep);
    }
  }

  // Consulta o CEP na API ViaCEP
  searchCep(cep: string): void {
    this.viaCepService.getCep(cep).subscribe({
      next: (result: ViacepResponse) => {
        if(result.erro) {
          this.mensagemErro = 'CEP não encontrado.';
          this.mensagemSucesso = '';
        } else {
          this.mensagemErro = '';
          this.address.cep = result.cep;
          this.address.street = result.logradouro;
          this.address.city = result.localidade;
          this.address.neighborhood = result.bairro;
          this.address.uf = result.uf;
          this.address.state = result.estado;
        }
      },
      error: (error: HttpErrorResponse) => {
        this.mensagemSucesso = '';

        if(error.status === 0) {
          this.mensagemErro = 'Falha ao se conectar com o serviço ViaCEP.';
          return;
        } else if(error.status === 500) {
          this.mensagemErro = 'O servidor está temporariamente indisponível.';
          return;
        } else {
          this.mensagemErro = 'Não foi possível consultar o CEP.';
          return;
        }
      }
    });
  }

  clearAddressFromCep(): void {
    this.address.street = '';
    this.address.neighborhood = '';
    this.address.city = '';
    this.address.uf = '';
    this.address.state = '';
  }
  
}