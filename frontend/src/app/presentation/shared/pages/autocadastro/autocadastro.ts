import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Viacep } from '../../../../application/client/services/viacep';
import { HttpErrorResponse } from '@angular/common/http';
import { ViacepResponse } from '../../../../domain/address/models/viacep-response.model';
import { Address } from '../../../../domain/address/models/address.model';
import { CreateClient } from '../../../../domain/client/models/create-client.model';
import { NgxMaskDirective } from 'ngx-mask';
import { ClientService } from '../../../../application/client/services/client-service';

import Decimal from 'decimal.js'

@Component({
  selector: 'app-autocadastro',
  imports: [FormsModule, NgxMaskDirective],
  templateUrl: './autocadastro.html',
  styleUrl: './autocadastro.css',
})
export class Autocadastro {

  public salarioExibicao: string = '';
  public ruaBloqueada: boolean = false;
  
  public address : Address = {
    cep: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    uf: '',
    state: ''
  };

    public client: CreateClient = {
    nome: '',
    email: '',
    cpf: '',
    telefone: '',
    salario: new Decimal(0),
    endereco: this.address
  };

  public mensagemSucesso: string = '';
  public mensagemErro: string = '';

  constructor(private viaCepService : Viacep,
              private clientService : ClientService,
              private changeDetector: ChangeDetectorRef
  ) {}

  // Bloqueia caracteres não numéricos nos campos de CPF, telefone e salário.
  public bloquearNaoNumeros(event: KeyboardEvent): void {
    const tecla = event.key;
    const permitido = /\d/.test(tecla) || tecla === 'Backspace' || tecla === 'Delete' || tecla === 'ArrowLeft' || tecla === 'ArrowRight' || tecla === 'Tab' || tecla === 'Shift';

    if (!permitido) {
      event.preventDefault();
    }
  }

  // Formata o CPF para xxx.xxx.xxx-xx
  public formatarCPF(): void {
    let cpfNumeros = this.client.cpf.replace(/\D/g, '');

    if (cpfNumeros.length > 11) {
      cpfNumeros = cpfNumeros.slice(0, 11);
    }

    cpfNumeros = cpfNumeros.replace(/(\d{3})(\d)/, '$1.$2');
    cpfNumeros = cpfNumeros.replace(/(\d{3})(\d)/, '$1.$2');
    cpfNumeros = cpfNumeros.replace(/(\d{3})(\d{1,2})$/, '$1-$2');

    this.client.cpf = cpfNumeros;

  }

  // Formata o telefone com DDD e traço
  public formatarTelefone(): void {
    let telefoneNumeros = this.client.telefone.replace(/\D/g, '');

    if (telefoneNumeros.length > 11) {
      telefoneNumeros = telefoneNumeros.slice(0, 11);
    }

    telefoneNumeros = telefoneNumeros.replace(/(\d{2})(\d)/, '($1) $2');
    telefoneNumeros = telefoneNumeros.replace(/(\d{5})(\d)/, '$1-$2');

    this.client.telefone = telefoneNumeros;
  }

  // Formata o salário para exibição e mantém o valor numérico para envio
  public formatarSalario(): void {
    const salarioNumeros = this.salarioExibicao.replace(/\D/g, '');

    if (!salarioNumeros) {
      this.salarioExibicao = '';
      this.client.salario = new Decimal(0);
      return;
    }

    this.client.salario = new Decimal(salarioNumeros).div(100); // alterado para decimal de acordo com enunciado do trabalho
    this.salarioExibicao = this.client.salario.toNumber().toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  public onSubmit(): void {
    if (!this.client.nome || !this.client.cpf || !this.client.email) {
      this.mensagemErro = 'Por favor, preencha os campos obrigatórios (*).';
      this.mensagemSucesso = '';
      return;
    }

    if (this.client.salario.comparedTo(0) <= 0) {
      this.mensagemErro = 'O salário deve ser um valor positivo.';
      this.mensagemSucesso = '';
      return;
    }

    this.mensagemErro = '';
    this.mensagemSucesso = '';

    const payload = {
      ...this.client,
      cpf: this.client.cpf.replace(/\D/g, ''),
      salario: this.client.salario.toFixed(2)
    };

    // Envia o cliente para o service
    this.clientService.createClientRequest(payload as any).subscribe({
      next: () => {
        this.mensagemErro = '';
        this.mensagemSucesso = 'Solicitação de autocadastro enviada com sucesso! Aguarde a análise do gerente.'
        this.changeDetector.detectChanges();
      },
      error: (error: HttpErrorResponse) => {
        this.mensagemSucesso = '';

        let errorObject = error.error;
        if (typeof errorObject === 'string') {
          try {
            errorObject = JSON.parse(errorObject);
          } catch (e) {}
        }
        
        if (errorObject && errorObject.mensagem) {
          this.mensagemErro = errorObject.mensagem;
        } else if (error.status === 400) {
          this.mensagemErro = 'Dados inválidos enviados na requisição.';
        } else if (error.status === 409) {
          this.mensagemErro = 'CPF ou e-mail já possui cadastro ou solicitação.';
        } else if (error.status === 500) {
          this.mensagemErro = 'O servidor está temporariamente indisponível.';
        } else {
          this.mensagemErro = 'Não foi possível realizar a solicitação.';
        }
        this.changeDetector.detectChanges();
      }
    });
  }

  // Valida CEP antes da consulta na API ViaCEP
  verifyCep(cep: string): void {
    const cleanCep = cep.replace(/\D/g, '');

    if(cleanCep === '') {
      // Não mostra nada na tela para não atrapalhar a digitação
      this.clearAddressFromCep();
      this.mensagemErro = '';
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
          this.address.logradouro = result.logradouro;
          this.ruaBloqueada = result.logradouro !== '';
          this.address.cidade = result.localidade;
          this.address.bairro = result.bairro;
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
    this.address.logradouro = '';
    this.address.bairro = '';
    this.address.cidade = '';
    this.address.uf = '';
    this.address.state = '';
    this.ruaBloqueada = false;
  }
  
}