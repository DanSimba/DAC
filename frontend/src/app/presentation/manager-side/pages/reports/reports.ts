import { Component, computed, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

interface LinhaRelatorio {
  // só pro mock não ficar tão zoado e quem sabe dá pra usar depois
  cpfCliente: string;
  nomeCliente: string;
  emailCliente: string;
  salario: number;
  saldoConta: number;
  numeroConta: string;
  cpfGerente: string;
  nomeGerente: string;
}

@Component({
  selector: 'app-reports',
  imports: [CurrencyPipe],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class Reports {

  //MOCKZIN - vulgo preciso do API Composition: ms-cliente + ms-conta + ms-gerente
  private linhas = signal<LinhaRelatorio[]>([
    { cpfCliente: '111.111.111-11', nomeCliente: 'Clawdeen Wolf', emailCliente: 'clawdeen@bantads.com', salario: 2800, saldoConta: 5230.5, numeroConta: '4821', cpfGerente: '999.999.999-99', nomeGerente: 'Dievalson Oracle Pereira' },
    { cpfCliente: '222.222.222-22', nomeCliente: 'Cleo de Nile', emailCliente: 'cleo@bantads.com', salario: 5300, saldoConta: 12040.9, numeroConta: '1093', cpfGerente: '999.999.999-99', nomeGerente: 'Dievalson Oracle Pereira' },
    { cpfCliente: '333.333.333-33', nomeCliente: 'Draculaura', emailCliente: 'draculaura@bantads.com', salario: 4100, saldoConta: 870.15, numeroConta: '7345', cpfGerente: '888.888.888-88', nomeGerente: 'Lagoona Blue' },
    { cpfCliente: '444.444.444-44', nomeCliente: 'Frankie Stein', emailCliente: 'frankie@bantads.com', salario: 3200, saldoConta: 2200, numeroConta: '2210', cpfGerente: '888.888.888-88', nomeGerente: 'Lagoona Blue' },
  ]);

  // Ordenado por nome do cliente ala RF11
  linhasOrdenadas = computed(() =>
    [...this.linhas()].sort((a, b) => a.nomeCliente.localeCompare(b.nomeCliente))
  );
}
