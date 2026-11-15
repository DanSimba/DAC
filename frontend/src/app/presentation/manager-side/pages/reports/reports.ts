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