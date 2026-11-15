import { ChangeDetectorRef, Component, OnInit, inject} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClienteConsulta } from '../../../../domain/manager/models/consulta-cliente.model';
import { ManagerHttpService } from '../../../../infraestructure/http/manager.http.service';

@Component({
  selector: 'app-clients',
  imports: [FormsModule],
  templateUrl: './clients.html',
  styleUrl: './clients.css',
})
export class Clients implements OnInit {

  private managerService = inject(ManagerHttpService);
  private cdr = inject(ChangeDetectorRef);

  public clientes: ClienteConsulta[] = [];


  public termoBusca: string = '';
  public paginaAtual: number = 1;
  public itensPorPagina: number = 5;

  ngOnInit(): void {
    this.carregarClientes();
  }

  public carregarClientes(): void {
    this.managerService.listarClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao listar clientes:', erro);
      }
    });
  }

  public get getClientesFiltradosEOrdenados(): ClienteConsulta[] {
    const termo = this.termoBusca.toLowerCase().trim();

    const filtrados = this.clientes.filter(cliente => {
      const nomeMatch = cliente.nome.toLowerCase().includes(termo);
      const cpfLimpo = cliente.cpf.replace(/\D/g, '');
      const termoLimpo = termo.replace(/\D/g, '');
      const cpfMatchNum = cpfLimpo.includes(termoLimpo) && termoLimpo !== '';
      const cpfMatchOriginal = cliente.cpf.includes(termo);

      return nomeMatch || cpfMatchNum || cpfMatchOriginal;
    });

    return filtrados.sort((a, b) => a.nome.localeCompare(b.nome));
  }

  //reaproveitando paginação do da home do gerente, to testando
  public get getClientesPaginados(): ClienteConsulta[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina; 
    return this.getClientesFiltradosEOrdenados.slice(inicio, fim);
  }
  
  public get getTotalPaginas(): number {
    return Math.ceil(this.getClientesFiltradosEOrdenados.length / this.itensPorPagina) || 1;
  }

  public mudarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.getTotalPaginas) {
      this.paginaAtual = pagina;
    }
  }

  public resetarPagina(): void {
    this.paginaAtual = 1;
  }

}