import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SolicitacaoCadastro } from '../../../../domain/manager/models/solicitacao-cadastro.model';

@Component({
  selector: 'app-manager-home-page',
  imports: [FormsModule],
  templateUrl: './manager-home-page.html',
  styleUrl: './manager-home-page.css',
})
export class ManagerHomePage {

  // mockzim de solicitações pra teste
  public solicitacoes: SolicitacaoCadastro[] = [
    {
      id: 1,
      dataHora: '20/09/2026 08:30',
      cpf: '123.456.789-01',
      nome: 'Monster Truck',
      salario: 'R$ 4.500,00',
      status: 'PENDENTE'
    },
    {
      id: 2,
      dataHora: '19/09/2026 17:45',
      cpf: '987.654.321-00',
      nome: 'Monster Zero',
      salario: 'R$ 8.200,50',
      status: 'PENDENTE'
    },
    {
      id: 3,
      dataHora: '18/09/2026 14:10',
      cpf: '456.789.123-44',
      nome: 'Moster Hunter',
      salario: 'R$ 3.100,00',
      status: 'APROVADO'
    }
  ];

  public mensagemSucesso: string = '';
  public mensagemErro: string = '';

  //pop-up de rejeição
  public modalAberto: boolean = false;
  public solicitacaoSelecionada: SolicitacaoCadastro | null = null;
  public motivoRejeicao: string = '';

  // Aprovar solicitação
  public aprovar(solicitacao: SolicitacaoCadastro): void {
    solicitacao.status = 'APROVADO';
    this.mensagemSucesso = 'Solicitação aprovada com sucesso!';
    this.mensagemErro = '';
  }

  //Abre o pop-up pra digitar o motivo da recusa
  public abrirModal(solicitacao: SolicitacaoCadastro): void {
    this.solicitacaoSelecionada = solicitacao;
    this.motivoRejeicao = '';
    this.modalAberto = true;
  }

  // Fecha o pop-up limpando os campos
  public fecharModal(): void {
    this.modalAberto = false;
    this.solicitacaoSelecionada = null;
    this.motivoRejeicao = '';
  }

  // Confirma a recusa salvando o motivo
  public confirmarRejeicao(): void {
    if (!this.motivoRejeicao) {
      this.mensagemErro = 'Digite o motivo da rejeição.';
      return;
    }

    if (this.solicitacaoSelecionada) {
      this.solicitacaoSelecionada.status = 'REJEITADO';
      this.solicitacaoSelecionada.motivoRejeicao = this.motivoRejeicao;
      this.mensagemSucesso = 'Solicitação rejeitada com sucesso.';
      this.mensagemErro = '';
    }

    this.fecharModal();
  }

}