import { randomUUID } from 'node:crypto';
import { config } from '../config.js';
import { request } from '../infra/http-client.js';
import { redis } from '../infra/redis.js';
import { buscarContas } from './conta.service.js';
import { buscarGerentes } from './gerente.service.js';

const JOB_TTL_SECONDS = 300;

function clienteUrl(field) {
  const params = new URLSearchParams();

  if (field) {
    params.set('field', field);
  }

  const query = params.toString();
  const url = `${config.serviceUrls.cliente}/clientes`;

  return query ? `${url}?${query}` : url;
}

async function buscarClientes(field) {
  const { data } = await request(clienteUrl(field), {
    unavailableMessage: 'MS Cliente indisponível.'
  });

  return data ?? [];
}

export async function listar({ busca, field } = {}) {
  const filtro = busca ?? field;
  const [clientes, contas] = await Promise.all([
    buscarClientes(filtro),
    buscarContas()
  ]);

  const contasPorCpf = new Map(
    contas.map(conta => [String(conta.client_cpf), conta])
  );

  return clientes
    .map(cliente => {
      const conta = contasPorCpf.get(String(cliente.cpf));

      return {
        cpf: cliente.cpf,
        nome: cliente.nome,
        cidade: cliente.cidade,
        estado: cliente.estado,
        saldo: Number(conta?.balanco ?? 0).toFixed(2)
      };
    })
    .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
}

export async function buscarPorCpf(cpf) {
  const { data } = await request(
    `${config.serviceUrls.cliente}/clientes/findByCpf/${encodeURIComponent(cpf)}`,
    { unavailableMessage: 'MS Cliente indisponível.' }
  );

  return {
    cpf: data.cpf,
    nome: data.nome,
    email: data.email,
    telefone: data.telefone,
    salario: Number(data.salario ?? 0).toFixed(2),
    endereco: {
      logradouro: data.logradouro,
      numero: String(data.numero),
      complemento: data.complemento ?? null,
      cep: data.cep,
      cidade: data.cidade,
      uf: data.estado
    }
  };
}

async function montarRelatorio() {
  const [clientes, contas, gerentes] = await Promise.all([
    buscarClientes(),
    buscarContas(),
    buscarGerentes()
  ]);

  const contasPorCpf = new Map(
    contas.map(conta => [String(conta.client_cpf), conta])
  );
  const gerentesPorId = new Map(
    gerentes.map(gerente => [String(gerente.id), gerente])
  );
  const gerentesPorCpf = new Map(
    gerentes.map(gerente => [String(gerente.cpf), gerente])
  );

  return clientes.map(cliente => {
    const conta = contasPorCpf.get(String(cliente.cpf));
    const gerenteId = conta?.managerId === undefined
      ? undefined
      : String(conta.managerId);
    const gerente = gerenteId
      ? gerentesPorId.get(gerenteId) ?? gerentesPorCpf.get(gerenteId)
      : undefined;

    return {
      cpf: cliente.cpf,
      nome: cliente.nome,
      email: cliente.email,
      salario: Number(cliente.salario ?? 0).toFixed(2),
      numeroConta: conta?.number ?? '-',
      saldo: Number(conta?.balanco ?? 0).toFixed(2),
      cpfGerente: gerente?.cpf ?? '-',
      nomeGerente: gerente?.nome ?? '-'
    };
  });
}

async function processarRelatorio(jobId) {
  try {
    const clientes = await montarRelatorio();
    const redisClient = await redis.getClient();

    await redisClient.set(
      `job:${jobId}`,
      JSON.stringify({
        jobId,
        status: 'CONCLUIDO',
        resultType: 'inline',
        dominio: 'clientes',
        resourceId: null,
        resultado: { clientes: clientes.sort(
          (a, b) => a.nome.localeCompare(b.nome, 'pt-BR')
        ) },
        erro: null
      }),
      { EX: JOB_TTL_SECONDS }
    );
  } catch (error) {
    try {
      const redisClient = await redis.getClient();

      await redisClient.set(
        `job:${jobId}`,
        JSON.stringify({
          jobId,
          status: 'FALHA',
          resultType: null,
          dominio: 'clientes',
          resourceId: null,
          erro: error.message
        }),
        { EX: JOB_TTL_SECONDS }
      );
    } catch (redisError) {
      console.error(`Erro ao atualizar o job ${jobId}:`, redisError);
    }
  }
}

export async function gerarRelatorio() {
  const jobId = randomUUID();

  try {
    const redisClient = await redis.getClient();

    await redisClient.set(
      `job:${jobId}`,
      JSON.stringify({
        jobId,
        status: 'PENDENTE',
        resultType: null,
        dominio: 'clientes',
        resourceId: null,
        erro: null
      }),
      { EX: JOB_TTL_SECONDS }
    );
  } catch {
    const error = new Error('Redis indisponível.');
    error.status = 503;
    throw error;
  }

  processarRelatorio(jobId);

  return { jobId, status: 'PENDENTE' };
}
