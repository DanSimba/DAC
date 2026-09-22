// Model pra lista de solicitações de autocadastro na visão do gerente
export interface SolicitacaoCadastro {
  id: number;
  dataHora: string;
  cpf: string; // Exibido formatado como 123.456.789-00
  nome: string;
  salario: string; // Exibido formatado como R$ 2.500,00
  status: 'PENDENTE' | 'APROVADO' | 'REJEITADO';
  motivoRejeicao?: string;
}