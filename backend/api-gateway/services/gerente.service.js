import { config } from '../config.js';
import { HttpError, request } from '../infra/http-client.js';
import { iniciarSaga } from './saga.service.js';
import { buscarContas } from './conta.service.js';

const gerenteUrl = `${config.serviceUrls.gerente}/gerente`;

export async function buscarGerentes() {
  const { data } = await request(gerenteUrl, {
    unavailableMessage: 'MS Gerente indisponível.'
  });

  return data ?? [];
}

export async function listar() {
  const [gerentes, contas] = await Promise.all([
    buscarGerentes(),
    buscarContas()
  ]);

  return gerentes
    .map(gerente => ({
      ...gerente,
      quantClientes: contas.filter(
        conta => String(conta.managerId) === String(gerente.id)
      ).length
    }))
    .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
}

export async function buscar(identificador) {
  const gerentes = await buscarGerentes();
  const gerente = gerentes.find(item =>
    String(item.id) === String(identificador)
    || String(item.cpf) === String(identificador)
  );

  if (!gerente) {
    throw new HttpError(404, 'Gerente não encontrado.');
  }

  return gerente;
}

export async function criar(gerente) {
  return iniciarSaga({
    tipo: 'inserir-gerente',
    payload: gerente,
    dominio: 'gerentes',
    resourceId: gerente.cpf
  });
}

export async function atualizar(id, gerente) {
  const { data } = await request(
    `${gerenteUrl}/${encodeURIComponent(id)}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(gerente),
      unavailableMessage: 'MS Gerente indisponível.'
    }
  );

  return data;
}

export async function remover(id, cpfAutenticado) {
  const gerente = await buscar(id);

  if (String(gerente.cpf) === String(cpfAutenticado)) {
    throw new HttpError(403, 'Um gerente não pode remover a si mesmo.');
  }

  return iniciarSaga({
    tipo: 'remover-gerente',
    payload: {
      id: gerente.id,
      cpf: gerente.cpf
    },
    dominio: 'gerentes'
  });
}
