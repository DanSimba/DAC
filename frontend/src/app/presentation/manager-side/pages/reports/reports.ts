import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RelatorioCliente } from '../../../../domain/manager/models/relatorio-cliente.model';
import { ManagerHttpService } from '../../../../infraestructure/http/manager.http.service';

@Component({
  selector: 'app-reports',
  imports: [],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class Reports implements OnInit {

  /*
  // Mockzim pra testar a tela
  public relatorio: RelatorioCliente[] = [
    {
      cpfCliente: '111.111.111-11',
      nomeCliente: 'Clawdeen Wolf',
      emailCliente: 'clawdeen@bantads.com',
      salario: 'R$ 2.800,00',
      numeroConta: '4821',
      saldoConta: 'R$ 5.230,50',
      cpfGerente: '999.999.999-99',
      nomeGerente: 'Dievalson Oracle Pereira'
    },
    {
      cpfCliente: '222.222.222-22',
      nomeCliente: 'Cleo de Nile',
      emailCliente: 'cleo@bantads.com',
      salario: 'R$ 5.300,00',
      numeroConta: '1093',
      saldoConta: 'R$ 12.040,90',
      cpfGerente: '999.999.999-99',
      nomeGerente: 'Dievalson Oracle Pereira'
    },
    {
      cpfCliente: '333.333.333-33',
      nomeCliente: 'Draculaura',
      emailCliente: 'draculaura@bantads.com',
      salario: 'R$ 4.100,00',
      numeroConta: '7345',
      saldoConta: 'R$ 870,15',
      cpfGerente: '888.888.888-88',
      nomeGerente: 'Lagoona Blue'
    },
    {
      cpfCliente: '444.444.444-44',
      nomeCliente: 'Frankie Stein',
      emailCliente: 'frankie@bantads.com',
      salario: 'R$ 3.200,00',
      numeroConta: '2210',
      saldoConta: 'R$ 2.200,00',
      cpfGerente: '888.888.888-88',
      nomeGerente: 'Lagoona Blue'
    }
  ];
  */

  private managerService = inject(ManagerHttpService);
  private cdr = inject(ChangeDetectorRef);

  public relatorio: RelatorioCliente[] = [];

  public paginaAtual: number = 1;
  public itensPorPagina: number = 5;

  ngOnInit(): void {
    this.carregarRelatorio();
  }

  public carregarRelatorio(): void {
    this.managerService.listarRelatorioClientes().subscribe({
      next: (dados) => {
        this.relatorio = dados;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar relatório de clientes:', erro);
      }
    });
  }

  // continua ordenando de forma crescente por nome do cliente
  public get getRelatorioOrdenado(): RelatorioCliente[] {
    return [...this.relatorio].sort((a, b) => a.nomeCliente.localeCompare(b.nomeCliente));
  }

  // paginação de 5 em 5 igual as outras telas do gerente
  public get getRelatorioPaginado(): RelatorioCliente[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.getRelatorioOrdenado.slice(inicio, fim);
  }

  public get getTotalPaginas(): number {
    return Math.ceil(this.getRelatorioOrdenado.length / this.itensPorPagina) || 1;
  }

  public mudarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.getTotalPaginas) {
      this.paginaAtual = pagina;
    }
  }

}