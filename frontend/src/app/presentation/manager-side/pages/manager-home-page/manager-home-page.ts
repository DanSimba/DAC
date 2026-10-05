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

  // Mockzim pra testar a tela rlx
  public solicitacoes: SolicitacaoCadastro[] = [
    {
      dataHora: '18/09/2026 14:10',
      cpf: '456.789.123-44',
      nome: 'Moster Hunter',
      salario: 'R$ 3.100,00',
      status: 'APROVADO'
    },
    {
      dataHora: '19/09/2026 17:45',
      cpf: '987.654.321-00',
      nome: 'Monster Zero',
      salario: 'R$ 8.200,50',
      status: 'PENDENTE'
    },
    {
      dataHora: '20/09/2026 08:30',
      cpf: '123.456.789-01',
      nome: 'Monster Truck',
      salario: 'R$ 4.500,00',
      status: 'PENDENTE'
    },
    {
      dataHora: '21/09/2026 09:15',
      cpf: '111.222.333-47',
      nome: 'Draculaura Silva',
      salario: 'R$ 5.000,00',
      status: 'PENDENTE'
    },
    {
      dataHora: '22/09/2026 11:00',
      cpf: '555.666.777-85',
      nome: 'Frankie Stein',
      salario: 'R$ 3.500,00',
      status: 'PENDENTE'
    },
    {
      dataHora: '17/09/2026 10:00',
      cpf: '123.123.123-14',
      nome: 'Cleiton',
      salario: 'R$ 1.500,00',
      status: 'REJEITADO',
      motivoRejeicao: 'Renda incompatível com o declarado.',
      dataHoraDecisao: '18/09/2026 09:00'
    },
    {
      dataHora: '18/09/2026 14:10',
      cpf: '456.789.123-43',
      nome: 'Monster Bike',
      salario: 'R$ 3.100,00',
      status: 'APROVADO'
    },
    {
      dataHora: '19/09/2026 17:45',
      cpf: '987.654.321-01',
      nome: 'Monster Mango Loco',
      salario: 'R$ 8.200,50',
      status: 'PENDENTE'
    },
    {
      dataHora: '20/09/2026 08:30',
      cpf: '123.456.789-02',
      nome: 'Monster Car',
      salario: 'R$ 4.500,00',
      status: 'PENDENTE'
    },
    {
      dataHora: '21/09/2026 09:15',
      cpf: '111.222.333-46',
      nome: 'Mostro do lago',
      salario: 'R$ 5.000,00',
      status: 'PENDENTE'
    },
    {
      dataHora: '22/09/2026 11:00',
      cpf: '555.666.777-89',
      nome: 'Mostro do pântano',
      salario: 'R$ 3.500,00',
      status: 'PENDENTE'
    },
    {
      dataHora: '17/09/2026 10:00',
      cpf: '123.123.123-19',
      nome: 'Alma Sebosa',
      salario: 'R$ 1.500,00',
      status: 'REJEITADO',
      motivoRejeicao: 'Renda incompatível com o declarado.',
      dataHoraDecisao: '18/09/2026 09:00'
    },
    {
      dataHora: '25/09/2026 16:00',
      cpf: '123.125.123-10',
      nome: 'Mostro Debaixo da cama',
      salario: 'R$ 1.500,00',
      status: 'REJEITADO',
      motivoRejeicao: 'Documento ilegivel.',
      dataHoraDecisao: '26/09/2026 09:00'
    },
    {
      dataHora: '26/09/2026 15:00',
      cpf: '123.032.123-19',
      nome: 'Serasa',
      salario: 'R$ 1.500,00',
      status: 'APROVADO',
    }
  ];

  // Controle do pop-up
  public modalAberto: boolean = false;
  public modoModal: 'REJEITAR' | 'VER_MOTIVO' = 'REJEITAR';
  public solicitacaoSelecionada: SolicitacaoCadastro | null = null;
  public motivoRejeicao: string = '';
  public motivoRejeicaoErro: string = '';

  // Filtros e Paginação
  public filtroAtual: 'PENDENTE' | 'APROVADO' | 'REJEITADO' = 'PENDENTE';
  public paginaAtual: number = 1;
  public itensPorPagina: number = 5;

  //função pra transformar string 'dd/MM/yyyy HH:mm' pro sort ordenar
  public converterData(data: string): Date {
    const [date, time] = data.split(' ');
    const [day, month, year] = date.split('/');
    const [hour, minute] = time.split(':');
    return new Date(Number(year), Number(month) - 1, Number(day), Number(hour), Number(minute));
  }

  // ordena da solicitaão mais antigo para o mais recente em todos os filtros
  public get getSolicitacoesOrdenadas(): SolicitacaoCadastro[] {
    const filtradas = this.solicitacoes.filter(s => s.status === this.filtroAtual);
    
    return filtradas.sort((a, b) => {
      return this.converterData(a.dataHora).getTime() - this.converterData(b.dataHora).getTime();
    });
  }

  //puxa só as solicitações da página atual, já filtradas e ordenadas 
  public get getSolicitacoesPaginadas(): SolicitacaoCadastro[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina; 
    return this.getSolicitacoesOrdenadas.slice(inicio, fim);
  }

  //Calcula o numero de paginas
  public get getTotalPaginas(): number {
    return Math.ceil(this.getSolicitacoesOrdenadas.length / this.itensPorPagina) || 1;
  }

  public mudarFiltro(filtro: 'PENDENTE' | 'APROVADO' | 'REJEITADO'): void {
    this.filtroAtual = filtro;
    this.paginaAtual = 1;
  }

  public mudarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.getTotalPaginas) {
      this.paginaAtual = pagina;
    }
  }

  public aprovar(solicitacao: SolicitacaoCadastro): void {
    solicitacao.status = 'APROVADO';
    solicitacao.dataHoraDecisao = new Date().toLocaleString('pt-BR');
    this.ajustarPaginaAposRemocao();
  }

  public abrirModalRejeitar(solicitacao: SolicitacaoCadastro): void {
    this.solicitacaoSelecionada = solicitacao;
    this.motivoRejeicao = '';
    this.motivoRejeicaoErro = '';
    this.modoModal = 'REJEITAR';
    this.modalAberto = true;
  }

  // Abre o modal apenas para leitura na aba de Rejeitados
  public abrirModalVerMotivo(solicitacao: SolicitacaoCadastro): void {
    this.solicitacaoSelecionada = solicitacao;
    this.motivoRejeicao = solicitacao.motivoRejeicao || 'Motivo não registrado.';
    this.motivoRejeicaoErro = '';
    this.modoModal = 'VER_MOTIVO';
    this.modalAberto = true;
  }

  public fecharModal(): void {
    this.modalAberto = false;
    this.solicitacaoSelecionada = null;
    this.motivoRejeicao = '';
    this.motivoRejeicaoErro = '';
  }

  public confirmarRejeicao(): void {
    if (!this.motivoRejeicao.trim()) {
      this.motivoRejeicaoErro = 'Digite o motivo da rejeição.';
      return;
    }

    if (this.solicitacaoSelecionada) {
      this.solicitacaoSelecionada.status = 'REJEITADO';
      this.solicitacaoSelecionada.motivoRejeicao = this.motivoRejeicao;
      this.solicitacaoSelecionada.dataHoraDecisao = new Date().toLocaleString('pt-BR');
      this.ajustarPaginaAposRemocao();
    }
    this.fecharModal();
  }

  //ajusta a pagina depois de rejeitar ou aprovar uma solicitacao pra evitar que exiba uma página vazia
  private ajustarPaginaAposRemocao(): void {
    if (this.getSolicitacoesPaginadas.length === 0 && this.paginaAtual > 1) {
      this.paginaAtual--;
    }
  }

}