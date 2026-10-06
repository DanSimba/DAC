import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClienteConsulta } from '../../../../domain/manager/models/consulta-cliente.model';

@Component({
  selector: 'app-clients',
  imports: [FormsModule],
  templateUrl: './clients.html',
  styleUrl: './clients.css',
})
export class Clients {

  public clientes: ClienteConsulta[] = [
    {
      cpf: '111.111.111-11',
      nome: 'Drácula da Silva',
      cidade: 'Curitiba',
      estado: 'PR',
      saldo: 'R$ 3.500,00'
    },
    {
      cpf: '222.222.222-22',
      nome: 'Lobisomem Guará',
      cidade: 'Goiânia',
      estado: 'GO',
      saldo: 'R$ 1.250,00'
    },
    {
      cpf: '333.333.333-33',
      nome: 'Múmia Paulista',
      cidade: 'São Paulo',
      estado: 'SP',
      saldo: 'R$ 5.400,00'
    },
    {
      cpf: '444.444.444-44',
      nome: 'Bicho Papão Júnior',
      cidade: 'Rio de Janeiro',
      estado: 'RJ',
      saldo: 'R$ 9.999,99'
    },
    {
      cpf: '555.555.555-55',
      nome: 'Alma Sebosa',
      cidade: 'Recife',
      estado: 'PE',
      saldo: 'R$ 15,50'
    },
    {
      cpf: '666.666.666-66',
      nome: 'Monstro Debaixo da Cama',
      cidade: 'Belo Horizonte',
      estado: 'MG',
      saldo: 'R$ 4.200,00'
    },
    {
      cpf: '777.777.777-77',
      nome: 'Zé do Caixão',
      cidade: 'Campinas',
      estado: 'SP',
      saldo: 'R$ 6.660,00'
    },
    {
      cpf: '888.888.888-88',
      nome: 'Bruxa do 71',
      cidade: 'Porto Alegre',
      estado: 'RS',
      saldo: 'R$ 71,00'
    },
    {
      cpf: '999.999.999-99',
      nome: 'Frankenstein de Souza',
      cidade: 'Manaus',
      estado: 'AM',
      saldo: 'R$ 2.300,00'
    },
    {
      cpf: '101.101.101-10',
      nome: 'Chupacabra Mineiro',
      cidade: 'Varginha',
      estado: 'MG',
      saldo: 'R$ 800,00'
    },
    {
      cpf: '121.121.121-12',
      nome: 'Saci de Patinete',
      cidade: 'Florianópolis',
      estado: 'SC',
      saldo: 'R$ 1.100,00'
    },
    {
      cpf: '131.131.131-13',
      nome: 'Fantasma da Firma',
      cidade: 'Brasília',
      estado: 'DF',
      saldo: 'R$ 4.500,00'
    },
    {
      cpf: '141.141.141-14',
      nome: 'Monstro do Pantanal',
      cidade: 'Cuiabá',
      estado: 'MT',
      saldo: 'R$ 8.900,00'
    },
    {
      cpf: '151.151.151-15',
      nome: 'Vampiro Pobre',
      cidade: 'Salvador',
      estado: 'BA',
      saldo: 'R$ 2,50'
    },
    {
      cpf: '161.161.161-16',
      nome: 'Cuca Assustadora',
      cidade: 'Fortaleza',
      estado: 'CE',
      saldo: 'R$ 12.000,00'
    }
  ];

  public termoBusca: string = '';

  public paginaAtual: number = 1;
  public itensPorPagina: number = 5;

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