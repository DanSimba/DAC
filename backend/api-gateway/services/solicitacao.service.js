import { config } from '../config.js';
import { request } from '../infra/http-client.js';
import { iniciarSaga } from './saga.service.js';

function removerLinks(resource) {
  if (!resource || typeof resource !== 'object') {
    return resource;
  }

  const { _links, ...data } = resource;
  return data;
}

function solicitacoesUrl(status) {
  const url = `${config.serviceUrls.cliente}/solicitacoes`;

  if (!status) {
    return url;
  }

  const params = new URLSearchParams({ status });
  return `${url}?${params.toString()}`;
}

export async function criar(solicitacao) {
  const { data } = await request(solicitacoesUrl(), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(solicitacao),
    unavailableMessage: 'MS Cliente indisponível.'
  });

  return removerLinks(data);
}

export async function listar(status) {
  const { data } = await request(solicitacoesUrl(status), {
    unavailableMessage: 'MS Cliente indisponível.'
  });
  const solicitacoes = data?.solicitacoes ?? data ?? [];

  return solicitacoes.map(removerLinks);
}

export async function buscarPorCpf(cpf) {
  const { data } = await request(
    `${solicitacoesUrl()}/${encodeURIComponent(cpf)}`,
    { unavailableMessage: 'MS Cliente indisponível.' }
  );

  return removerLinks(data);
}

export async function rejeitar(cpf, rejeicao) {
  const { data } = await request(
    `${solicitacoesUrl()}/${encodeURIComponent(cpf)}/rejeicao`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(rejeicao),
      unavailableMessage: 'MS Cliente indisponível.'
    }
  );

  return removerLinks(data);
}

export async function aprovar(cpf) {
  return iniciarSaga({
    tipo: 'aprovar-cliente',
    payload: { cpf },
    dominio: 'clientes',
    resourceId: cpf
  });
}
