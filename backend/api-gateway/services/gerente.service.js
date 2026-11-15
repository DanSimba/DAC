import { config } from '../config.js';
import { request } from '../infra/http-client.js';

export async function buscarGerentes() {
  const { data } = await request(`${config.serviceUrls.gerente}/gerente`, {
    unavailableMessage: 'MS Gerente indisponível.'
  });

  return data ?? [];
}
