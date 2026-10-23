import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SolicitacaoCadastro } from '../../../../domain/manager/models/solicitacao-cadastro.model';
import { ManagerHttpService } from '../../../../infraestructure/http/manager.http.service';

@Component({
  selector: 'app-manager-home-page',
  imports: [FormsModule],
  templateUrl: './manager-home-page.html',
  styleUrl: './manager-home-page.css',
})
export class ManagerHomePage implements OnInit {

  /*
  // Mockzim pra testar a tela rlx
  public solicitacoes: SolicitacaoCadastro[] = [
    {
      cpf: '856.789.123-44',
      nome: 'Moster Hunter 2',
      salario: 'R$ 3.200,00',
      status: 'APROVADO',
      dataHoraDecisao: '18/09/2026 15:30'
    },
    {
      cpf: '967.654.321-00',
      nome: 'Monster Zero Quente',
      salario: 'R$ 8.280,50',
      status: 'PENDENTE'
    },
    {
      cpf: '923.456.789-01',
      nome: 'Monster Truck Jr',
      salario: 'R$ 4.590,00',
      status: 'PENDENTE'
    },
    {
      cpf: '811.222.333-47',
      nome: 'Draculaura Da Silva',
      salario: 'R$ 5.900,00',
      status: 'PENDENTE'
    },
    {
      cpf: '755.666.777-85',
      nome: 'Frankie Estein',
      salario: 'R$ 3.300,00',
      status: 'PENDENTE'
    },
    {
      cpf: '523.123.123-14',
      nome: 'Cleiton Cleiton',
      salario: 'R$ 1.800,00',
      status: 'NAO_APROVADO',
      motivo: 'Renda incompatível com o declarado.',
      dataHoraDecisao: '18/09/2026 09:00'
    },
    {
      cpf: '656.789.123-43',
      nome: 'Monster Bike De Sousa',
      salario: 'R$ 3.200,00',
      status: 'APROVADO',
      dataHoraDecisao: '19/09/2026 10:15'
    },
    {
      cpf: '587.654.321-01',
      nome: 'Monster Mango Loco Gelado',
      salario: 'R$ 8.400,50',
      status: 'PENDENTE'
    },
    {
      cpf: '423.456.789-02',
      nome: 'Monster Car Doido',
      salario: 'R$ 4.800,00',
      status: 'PENDENTE'
    },
    {
      cpf: '111.322.333-46',
      nome: 'Mostro Do lago',
      salario: 'R$8.000,00',
      status: 'PENDENTE'
    },
    {
      cpf: '555.766.777-89',
      nome: 'Mostro Do pântano',
      salario: 'R$ 3.400,00',
      status: 'PENDENTE'
    },
    {
      cpf: '123.523.123-19',
      nome: 'Alma Sebosa Do Mal',
      salario: 'R$ 1.900,00',
      status: 'NAO_APROVADO',
      motivo: 'Renda incompatível com o declarado.',
      dataHoraDecisao: '18/09/2026 09:00'
    },
    {
      cpf: '223.125.123-10',
      nome: 'Mostro Debaixo da Cama',
      salario: 'R$ 1.300,00',
      status: 'NAO_APROVADO',
      motivo: 'Documento ilegível.',
      dataHoraDecisao: '26/09/2026 09:00'
    },
    {
      cpf: '223.032.123-19',
      nome: 'Serasa Negativado',
      salario: 'R$ 1.580,00',
      status: 'APROVADO',
      dataHoraDecisao: '26/09/2026 16:20'
    },
    {
      cpf: '173.082.193-19',
      nome: 'Barata Maluca',
      salario: 'R$ 1.550,00',
      status: 'APROVADO',
      dataHoraDecisao: '30/09/2026 11:00'
    },
    {
      cpf: '823.032.163-29',
      nome: 'Barrata',
      salario: 'R$ 2.850,00',
      status: 'APROVADO',
      dataHoraDecisao: '28/09/2026 17:00'
    },
    {
      cpf: '225.036.124-13',
      nome: 'Barata Voadora',
      salario: 'R$ 1.350,00',
      status: 'APROVADO',
      dataHoraDecisao: '29/09/2026 14:10'
    }
  ];
  */

  private managerService = inject(ManagerHttpService);

  public solicitacoes: SolicitacaoCadastro[] = [];

  public mensagemSucesso: string = '';
  public mensagemErro: string = '';


  //Controle do pop-up
  public modalAberto: boolean = false;
  public modoModal: 'RECUSAR' | 'VER_MOTIVO' = 'RECUSAR';
  public solicitacaoSelecionada: SolicitacaoCadastro | null = null;
  public motivo: string = '';
  public motivoErro: string = '';

  // Filtros e Paginação
  public filtroAtual: 'PENDENTE' | 'APROVADO' | 'NAO_APROVADO' = 'PENDENTE';
  public paginaAtual: number = 1;
  public itensPorPagina: number = 5;

  ngOnInit(): void {
    this.carregarSolicitacoes();
  }

  public carregarSolicitacoes(): void {
    this.managerService.listarSolicitacoes().subscribe({
      next: (solicitacoes) =>
        { this.solicitacoes = solicitacoes;
      },
      error: (erro) => {
        console.error('Erro ao carregar solicitações:', erro);
        this.mensagemErro = 'Falha ao carregar solicitações. Tente novamente.';
      }
    });
  }

  public aprovar(solicitacao: SolicitacaoCadastro): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    /*
    //teste com mock
    solicitacao.status = 'APROVADO';
    solicitacao.dataHoraDecisao = new Date().toLocaleString('pt-BR');
    this.mensagemSucesso = `A conta de ${solicitacao.nome} foi aprovada com sucesso!`;
    this.ajustarPaginaAposRemocao();
    */

     // /*
    this.managerService.aprovarSolicitacao(solicitacao.cpf).subscribe({
      next: () => {
        this.mensagemSucesso = `A conta de ${solicitacao.nome} foi aprovada com sucesso!`;
        this.carregarSolicitacoes(); 
        this.ajustarPaginaAposRemocao();
      },
      error: (erro) => {
        this.mensagemErro = 'Ocorreu um erro ao aprovar o cliente.';
      }
    });
    // */
  }

  public confirmarRecusa(): void {
    if (!this.motivo.trim()) {
      this.motivoErro = 'Digite o motivo da recusa.';
      return;
    }

    if (this.solicitacaoSelecionada) {

      /*
      //teste com mock
      this.solicitacaoSelecionada.status = 'NAO_APROVADO';
      this.solicitacaoSelecionada.motivo = this.motivo;
      this.solicitacaoSelecionada.dataHoraDecisao = new Date().toLocaleString('pt-BR');
      this.mensagemSucesso = `A solicitação de ${this.solicitacaoSelecionada.nome} foi recusada.`;
      this.fecharModal();
      this.ajustarPaginaAposRemocao();
      */


    // /*  
    const payload = { motivo: this.motivo }; 

      this.managerService.recusarSolicitacao(this.solicitacaoSelecionada.cpf, payload).subscribe({
        next: () => {
          this.mensagemSucesso = `A solicitação de ${this.solicitacaoSelecionada?.nome} foi recusada.`;
          this.fecharModal();
          this.carregarSolicitacoes();
          this.ajustarPaginaAposRemocao();
        },
        error: (erro) => {
          this.motivoErro = 'Ocorreu um erro ao recusar o cliente.';
        }
      });
      // */
    }
  }

  public get getSolicitacoesFiltradas(): SolicitacaoCadastro[] {
    const filtradas = this.solicitacoes.filter(s => s.status === this.filtroAtual);

    if (this.filtroAtual !== 'PENDENTE') {
      return filtradas.sort((a, b) => {
        if (!a.dataHoraDecisao) return 1;
        if (!b.dataHoraDecisao) return -1;

        return b.dataHoraDecisao.localeCompare(a.dataHoraDecisao);
      });
    }
    return filtradas;
  }


  //puxa só as solicitações da página atual, já filtradas e ordenadas 
  public get getSolicitacoesPaginadas(): SolicitacaoCadastro[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina; 
    return this.getSolicitacoesFiltradas.slice(inicio, fim);
  }

  //Calcula o numero de paginas
  public get getTotalPaginas(): number {
    return Math.ceil(this.getSolicitacoesFiltradas.length / this.itensPorPagina) || 1;
  }

  public mudarFiltro(filtro: 'PENDENTE' | 'APROVADO' | 'NAO_APROVADO'): void {
    this.filtroAtual = filtro;
    this.paginaAtual = 1;
  }

  public mudarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.getTotalPaginas) {
      this.paginaAtual = pagina;
    }
  }

  public abrirModalRecusa(solicitacao: SolicitacaoCadastro): void {
    this.solicitacaoSelecionada = solicitacao;
    this.motivo = '';
    this.motivoErro = '';
    this.modoModal = 'RECUSAR';
    this.modalAberto = true;
  }

  // Abre o modal apenas para leitura na aba de Não Aprovados
  public abrirModalVerMotivo(solicitacao: SolicitacaoCadastro): void {
    this.solicitacaoSelecionada = solicitacao;
    this.motivo = solicitacao.motivo || 'Motivo não registrado.';
    this.motivoErro = '';
    this.modoModal = 'VER_MOTIVO';
    this.modalAberto = true;
  }

  public fecharModal(): void {
    this.modalAberto = false;
    this.solicitacaoSelecionada = null;
    this.motivo = '';
    this.motivoErro = '';
  }

  //ajusta a pagina depois de recusar ou aprovar uma solicitacao pra evitar que exiba uma página vazia
  private ajustarPaginaAposRemocao(): void {
    if (this.getSolicitacoesPaginadas.length === 0 && this.paginaAtual > 1) {
      this.paginaAtual--;
    }
  }

}