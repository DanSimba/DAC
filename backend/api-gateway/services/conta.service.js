import { config } from '../config.js';
import { request } from '../infra/http-client.js';

const accountUrl = `${config.serviceUrls.account}/account`;

function dinheiro(valor) {
  return Number(valor ?? 0).toFixed(2);
}

function dataCriacao(valor) {
  const texto = String(valor ?? '');

  return /^\d{8}$/.test(texto)
    ? `${texto.slice(0, 4)}-${texto.slice(4, 6)}-${texto.slice(6, 8)}`
    : texto;
}

export function paraContratoPublico(conta) {
  return {
    numero: String(conta.number),
    cpfCliente: String(conta.client_cpf ?? conta.clientCpf),
    cpfGerente: String(conta.managerId),
    saldo: dinheiro(conta.balanco),
    dataCriacao: dataCriacao(conta.createdAt)
  };
}

function identityHeaders(usuario, withContentType = false) {
  const headers = {
    'X-User-CPF': String(usuario.cpf),
    'X-User-Tipo': String(usuario.tipo)
  };

  if (withContentType) {
    headers['Content-Type'] = 'application/json';
  }

  return headers;
}

async function buscarNomeCliente(cpf) {
  const { data } = await request(
    `${config.serviceUrls.cliente}/clientes/findNameByCpf/${encodeURIComponent(cpf)}`,
    { unavailableMessage: 'MS Cliente indisponível.' }
  );

  return data;
}

export async function buscarContas() {
  const { data } = await request(accountUrl, {
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data ?? [];
}

export async function buscarPorCpf(cpf, usuario) {
  const { data } = await request(
    `${accountUrl}/findByCpf/${encodeURIComponent(cpf)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data;
}

export async function buscarPorNumero(numero, usuario) {
  const { data } = await request(
    `${accountUrl}/findByNumber/${encodeURIComponent(numero)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data;
}

export async function buscarUltimaVersao(numero, usuario) {
  const { data } = await request(
    `${accountUrl}/getLastVersion/${encodeURIComponent(numero)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data;
}

export async function listarExtratos(numero, usuario) {
  const { data } = await request(
    `${accountUrl}/listExtratos/${encodeURIComponent(numero)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data ?? [];
}

export async function buscarExtrato(numero, usuario, { inicio, fim } = {}) {
  const dataFim = fim ? new Date(`${fim}T00:00:00`) : new Date();
  const dataInicio = inicio
    ? new Date(`${inicio}T00:00:00`)
    : new Date(dataFim.getTime() - 30 * 24 * 60 * 60 * 1000);
  const dias = (dataFim - dataInicio) / (24 * 60 * 60 * 1000);

  if (!Number.isFinite(dias) || dias < 0 || dias > 365) {
    const error = new Error('O intervalo do extrato deve ter no máximo 365 dias.');
    error.status = 422;
    throw error;
  }

  const extratos = await listarExtratos(numero, usuario);
  const movimentacoes = extratos.flatMap(extrato => [
    ...(extrato.opers ?? []).map(operacao => ({
      dataHora: operacao.datetime,
      tipo: operacao.side === 'DEP' ? 'DEPOSITO' : 'SAQUE',
      valor: dinheiro(operacao.value)
    })),
    ...(extrato.transfs ?? []).map(transferencia => ({
      dataHora: transferencia.datetime,
      tipo: 'TRANSFERENCIA',
      valor: dinheiro(transferencia.value),
      origem: {
        numeroConta: transferencia.accOrigin,
        cpf: transferencia.cpfOrigin,
        nome: transferencia.nameOrigin
      },
      destino: {
        numeroConta: transferencia.accDestiny,
        cpf: transferencia.cpfDestiny,
        nome: transferencia.nameDestiny
      }
    }))
  ]);
  const saldoAbertura = extratos.length > 0
    ? dinheiro(extratos[0].saldoApos)
    : dinheiro((await buscarPorNumero(numero, usuario)).balanco);

  return {
    numeroConta: String(numero),
    dataInicio: dataInicio.toISOString().slice(0, 10),
    dataFim: dataFim.toISOString().slice(0, 10),
    saldoAbertura,
    movimentacoes
  };
}

async function executarOperacao(tipo, operacao, usuario) {
  const body = {
    ...operacao,
    value: String(operacao.value),
    datetime: operacao.datetime ?? new Date().toISOString()
  };
  const { data } = await request(`${accountUrl}/${tipo}`, {
    method: 'POST',
    headers: identityHeaders(usuario, true),
    body: JSON.stringify(body),
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data;
}

export async function transferir(transferencia, usuario) {
  const cpfOrigem = String(usuario.cpf);
  const contaDestino = await buscarPorNumero(transferencia.accDestiny, usuario);
  const cpfDestino = String(contaDestino.client_cpf);
  const [nomeOrigem, nomeDestino] = await Promise.all([
    buscarNomeCliente(cpfOrigem),
    buscarNomeCliente(cpfDestino)
  ]);
  const body = {
    ...transferencia,
    cpfOrigin: cpfOrigem,
    cpfDestiny: cpfDestino,
    nameOrigin: nomeOrigem.nome,
    nameDestiny: nomeDestino.nome,
    value: String(transferencia.value),
    datetime: transferencia.datetime ?? new Date().toISOString()
  };
  const { data } = await request(`${accountUrl}/transfer`, {
    method: 'POST',
    headers: identityHeaders(usuario, true),
    body: JSON.stringify(body),
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data;
}

export async function sacar(operacao, usuario) {
  return executarOperacao('sacar', operacao, usuario);
}

export async function depositar(operacao, usuario) {
  return executarOperacao('depositar', operacao, usuario);
}

export async function realizarOperacaoPublica(tipo, numero, dados, usuario) {
  const conta = await buscarPorNumero(numero, usuario);

  if (String(conta.client_cpf) !== String(usuario.cpf)) {
    const error = new Error('Acesso negado.');
    error.status = 403;
    throw error;
  }

  const valor = String(dados?.valor ?? '');
  await (tipo === 'DEPOSITO' ? depositar : sacar)({
    accNumber: String(numero),
    value: valor,
    side: tipo === 'DEPOSITO' ? 'DEP' : 'SAC'
  }, usuario);

  return {
    numeroConta: String(numero),
    tipo,
    dataHora: new Date().toISOString(),
    valor
  };
}

export async function transferirPublico(numero, dados, usuario) {
  const origem = await buscarPorNumero(numero, usuario);

  if (String(origem.client_cpf) !== String(usuario.cpf)) {
    const error = new Error('Acesso negado.');
    error.status = 403;
    throw error;
  }

  if (String(numero) === String(dados?.contaDestino)) {
    const error = new Error('A conta de destino deve ser diferente da origem.');
    error.status = 422;
    throw error;
  }

  let destino;
  try {
    destino = await buscarPorNumero(dados?.contaDestino, usuario);
  } catch (error) {
    error.status = 422;
    throw error;
  }

  const nomeDestino = await buscarNomeCliente(destino.client_cpf);
  await transferir({
    accOrigin: String(numero),
    accDestiny: String(dados.contaDestino),
    value: String(dados.valor)
  }, usuario);

  return {
    numeroConta: String(numero),
    tipo: 'TRANSFERENCIA',
    dataHora: new Date().toISOString(),
    valor: String(dados.valor),
    destino: {
      numeroConta: String(dados.contaDestino),
      cpf: String(destino.client_cpf),
      nome: nomeDestino.nome
    }
  };
}
