// Model pra lista de solicitações de autocadastro na visão do gerente
export interface SolicitacaoCadastro {
  cpf: string; // Exibido formatado como 123.456.789-00
  nome: string;
  salario: string;
  status: 'PENDENTE' | 'APROVADO' | 'NAO_APROVADO';
  motivo?: string;
  dataHoraProcessamento?: string;
}