import { config } from '../config.js';
import { addLinks } from '../helpers/hateoas.js';
import { HttpError, request } from '../infra/http-client.js';

const accountUrl = `${config.serviceUrls.account}/account`;

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

async function obterPorCpf(cpf, usuario) {
  const { data } = await request(
    `${accountUrl}/findByCpf/${encodeURIComponent(cpf)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data;
}

async function obterPorNumero(numero, usuario) {
  const { data } = await request(
    `${accountUrl}/findByNumber/${encodeURIComponent(numero)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data;
}

async function obterUltimaVersao(numero, usuario) {
  const { data } = await request(
    `${accountUrl}/getLastVersion/${encodeURIComponent(numero)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data;
}

async function obterExtratos(numero, usuario) {
  const { data } = await request(
    `${accountUrl}/listExtratos/${encodeURIComponent(numero)}`,
    {
      headers: identityHeaders(usuario),
      unavailableMessage: 'MS Conta indisponível.'
    }
  );

  return data ?? [];
}

async function executarOperacao(tipo, operacao, usuario) {
  const body = {
    ...operacao,
    value: String(operacao.value)
  };
  const { data } = await request(`${accountUrl}/${tipo}`, {
    method: 'POST',
    headers: identityHeaders(usuario, true),
    body: JSON.stringify(body),
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data;
}

async function executarTransferencia(transferencia, usuario) {
  const cpfOrigem = String(usuario.cpf);
  const contaDestino = await obterPorNumero(transferencia.accDestiny, usuario);
  const cpfDestino = String(contaDestino.clientCpf);
  const [nomeOrigem, nomeDestino] = await Promise.all([
    buscarNomeCliente(cpfOrigem),
    buscarNomeCliente(cpfDestino)
  ]);
  const body = {
    ...transferencia,
    cpfOrigin: cpfOrigem,
    cpfDestiny: cpfDestino,
    nameOrigin: nomeOrigem,
    nameDestiny: nomeDestino,
    value: String(transferencia.value)
  };
  const { data } = await request(`${accountUrl}/transfer`, {
    method: 'POST',
    headers: identityHeaders(usuario, true),
    body: JSON.stringify(body),
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data;
}

function linksDaConta(conta) {
  const numero = encodeURIComponent(conta.number);

  return {
    self: `/account/findByNumber/${numero}`,
    extrato: `/account/listExtratos/${numero}`,
    ultimaVersao: `/account/getLastVersion/${numero}`,
    depositar: {
      href: '/account/depositar',
      method: 'POST'
    },
    sacar: {
      href: '/account/sacar',
      method: 'POST'
    },
    transferir: {
      href: '/account/transfer',
      method: 'POST'
    }
  };
}

function enviarResultado(res, data) {
  return data === undefined
    ? res.status(200).end()
    : res.status(200).json(data);
}

export async function buscarPorCpf(req, res) {
  if (
    req.usuario.tipo.toUpperCase() === 'CLIENTE'
    && String(req.usuario.cpf) !== String(req.params.cpf)
  ) {
    return res.status(403).json({ message: 'Acesso negado.' });
  }

  const conta = await obterPorCpf(req.params.cpf, req.usuario);
  const links = req.usuario.tipo.toUpperCase() === 'GERENTE'
    ? { self: `/account/findByCpf/${encodeURIComponent(conta.clientCpf)}` }
    : linksDaConta(conta);

  return res.status(200).json(addLinks(req, conta, links));
}

export async function buscarPorNumero(req, res) {
  const conta = await obterPorNumero(req.params.number, req.usuario);

  return res.status(200).json(addLinks(req, conta, linksDaConta(conta)));
}

export async function buscarUltimaVersao(req, res) {
  const versao = await obterUltimaVersao(req.params.number, req.usuario);

  return res.status(200).json(versao);
}

export async function listarExtratos(req, res) {
  const extratos = await obterExtratos(req.params.number, req.usuario);

  return res.status(200).json(extratos);
}

export async function sacar(req, res) {
  const resultado = await executarOperacao('sacar', req.body, req.usuario);

  return enviarResultado(res, resultado);
}

export async function depositar(req, res) {
  const resultado = await executarOperacao('depositar', req.body, req.usuario);

  return enviarResultado(res, resultado);
}

export async function transferir(req, res) {
  const resultado = await executarTransferencia(req.body, req.usuario);

  return enviarResultado(res, resultado);
}

export async function operar(req, res) {
  const operacao = String(req.body?.side).toUpperCase();

  if (operacao === 'DEP') {
    return depositar(req, res);
  }

  if (operacao === 'SAC') {
    return sacar(req, res);
  }

  throw new HttpError(400, 'Tipo de operação inválido.');
}
