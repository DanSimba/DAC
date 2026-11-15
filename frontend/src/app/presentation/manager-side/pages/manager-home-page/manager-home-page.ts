import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
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

  private managerService = inject(ManagerHttpService);
  private cdr = inject(ChangeDetectorRef);

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
      next: (resposta: any) => {
        this.solicitacoes = resposta.solicitacoes || [];
        
        for (const solicitacao of this.solicitacoes) {
          if (solicitacao.dataHoraProcessamento) {
            solicitacao.dataHoraProcessamento = new Date(solicitacao.dataHoraProcessamento).toLocaleString('pt-BR');
          }
        }

        this.cdr.markForCheck();
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar solicitações:', erro);
        this.mensagemErro = 'Falha ao carregar solicitações. Tente novamente.';
        this.cdr.markForCheck();
        this.cdr.detectChanges();
      }
    });
  }

  public aprovar(solicitacao: SolicitacaoCadastro): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    const cpfLimpo = solicitacao.cpf.replace(/\D/g, '');
    
    this.managerService.aprovarSolicitacao(cpfLimpo).subscribe({
      next: () => {
        solicitacao.status = 'APROVADO';
        solicitacao.dataHoraProcessamento = new Date().toLocaleString('pt-BR');
        this.mensagemSucesso = `A conta de ${solicitacao.nome} foi aprovada com sucesso!`;
        this.ajustarPaginaAposRemocao();
        this.cdr.detectChanges();
      },
      error: (erro) => {
        this.mensagemErro = 'Ocorreu um erro ao aprovar o cliente.';
        this.cdr.detectChanges(); 
      }
    });
  }

  public confirmarRecusa(): void {
    if (!this.motivo.trim()) {
      this.motivoErro = 'Digite o motivo da recusa.';
      return;
    }

    if (this.solicitacaoSelecionada) {

      const payload = { motivo: this.motivo }; 
      const cpfLimpo = this.solicitacaoSelecionada.cpf.replace(/\D/g, '');

      this.managerService.recusarSolicitacao(cpfLimpo, payload).subscribe({
        next: () => {
          this.mensagemSucesso = `A solicitação de ${this.solicitacaoSelecionada?.nome} foi recusada.`;
          this.fecharModal();
          this.carregarSolicitacoes();
          this.ajustarPaginaAposRemocao();
          this.cdr.detectChanges();
        },
        error: (erro) => {
          this.motivoErro = 'Ocorreu um erro ao recusar o cliente.';
          this.cdr.detectChanges();
        }
      });
    }
  }

  public get getSolicitacoesFiltradas(): SolicitacaoCadastro[] {
    const filtradas = this.solicitacoes.filter(s => s.status === this.filtroAtual);

    if (this.filtroAtual !== 'PENDENTE') {
      return filtradas.sort((a, b) => {
        if (!a.dataHoraProcessamento) return 1;
        if (!b.dataHoraProcessamento) return -1;

        return b.dataHoraProcessamento.localeCompare(a.dataHoraProcessamento);
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