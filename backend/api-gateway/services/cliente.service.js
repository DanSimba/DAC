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

export async function listar({ field } = {}) {
  const [clientes, contas] = await Promise.all([
    buscarClientes(field),
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
        saldo: String(conta?.balanco ?? '0')
      };
    })
    .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
}

export async function buscarPorCpf(cpf) {
  const { data } = await request(
    `${config.serviceUrls.cliente}/clientes/findByCpf/${encodeURIComponent(cpf)}`,
    { unavailableMessage: 'MS Cliente indisponível.' }
  );

  const { _links, ...cliente } = data;
  return cliente;
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
      cpfCliente: cliente.cpf,
      nomeCliente: cliente.nome,
      emailCliente: cliente.email,
      salario: String(cliente.salario ?? '0'),
      numeroConta: conta?.number ?? '-',
      saldoConta: String(conta?.balanco ?? '0'),
      cpfGerente: gerente?.cpf ?? '-',
      nomeGerente: gerente?.nome ?? '-'
    };
  });
}

async function processarRelatorio(jobId) {
  try {
    const resultado = await montarRelatorio();
    const redisClient = await redis.getClient();

    await redisClient.set(
      `job:${jobId}`,
      JSON.stringify({
        jobId,
        status: 'CONCLUIDO',
        resultType: 'inline',
        dominio: 'clientes',
        resourceId: null,
        resultado,
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

  return { jobId };
}
