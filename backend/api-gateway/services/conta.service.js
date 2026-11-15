import { config } from '../config.js';
import { request } from '../infra/http-client.js';

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

export async function transferir(transferencia, usuario) {
  const cpfOrigem = String(usuario.cpf);
  const contaDestino = await buscarPorNumero(transferencia.accDestiny, usuario);
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

export async function sacar(operacao, usuario) {
  return executarOperacao('sacar', operacao, usuario);
}

export async function depositar(operacao, usuario) {
  return executarOperacao('depositar', operacao, usuario);
}
