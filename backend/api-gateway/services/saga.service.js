import { randomUUID } from 'node:crypto';
import { rabbitMQ } from '../infra/rabbitmq.js';
import { redis } from '../infra/redis.js';

const queue = 'saga.cmd';
const jobTtlSeconds = 300;

function getJobKey(jobId) {
  return `job:${jobId}`;
}

export async function iniciarSaga({
  tipo,
  payload = {},
  dominio,
  resourceId = null
}) {
  if (!tipo) {
    throw new Error('Tipo da SAGA não informado.');
  }

  if (!dominio) {
    throw new Error('Domínio do job não informado.');
  }

  const sagaId = randomUUID();
  const redisClient = await redis.getClient();
  const jobKey = getJobKey(sagaId);
  const job = {
    jobId: sagaId,
    status: 'PENDENTE',
    resultType: null,
    dominio,
    resourceId,
    erro: null
  };

  await redisClient.set(jobKey, JSON.stringify(job), {
    EX: jobTtlSeconds
  });

  try {
    const rabbitMQChannel = await rabbitMQ.getChannel();

    await rabbitMQChannel.assertQueue(queue, {
      durable: true
    });

    rabbitMQChannel.sendToQueue(
      queue,
      Buffer.from(JSON.stringify({
        sagaId,
        tipo,
        timestamp: new Date().toISOString(),
        payload
      })),
      {
        persistent: true,
        contentType: 'application/json'
      }
    );
  } catch (error) {
    await redisClient.del(jobKey).catch(redisError => {
      console.error(`Erro ao remover o job ${sagaId}:`, redisError);
    });

    throw error;
  }

  return {
    jobId: sagaId,
    status: 'PENDENTE'
  };
}

export async function buscarStatus(jobId) {
  const redisClient = await redis.getClient();
  const job = await redisClient.get(getJobKey(jobId));

  return job ? JSON.parse(job) : null;
}

export async function buscarResultado(jobId) {
  const job = await buscarStatus(jobId);

  if (!job || job.status !== 'CONCLUIDO' || job.resultType !== 'inline') {
    return null;
  }

  return job.resultado;
}
