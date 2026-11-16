import { config } from '../config.js';
import { HttpError, request } from '../infra/http-client.js';
import { redis } from '../infra/redis.js';

async function reiniciarServico(url, nome) {
  await request(`${url}/reboot`, {
    method: 'POST',
    unavailableMessage: `${nome} indisponível.`
  });
}

async function limparRedis() {
  try {
    const redisClient = await redis.getClient();
    await redisClient.flushDb();
  } catch {
    throw new HttpError(503, 'Redis indisponível.');
  }
}

export async function reiniciar() {
  await Promise.all([
    reiniciarServico(config.serviceUrls.auth, 'MS Auth'),
    reiniciarServico(config.serviceUrls.cliente, 'MS Cliente'),
    reiniciarServico(config.serviceUrls.account, 'MS Conta'),
    reiniciarServico(config.serviceUrls.gerente, 'MS Gerente')
  ]);

  await limparRedis();

  return {
    status: 'ok',
    clientes: 5,
    gerentes: 4,
    contas: 5
  };
}
