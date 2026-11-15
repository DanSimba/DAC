import { config } from '../config.js';
import { request } from '../infra/http-client.js';

export async function buscarContas() {
  const { data } = await request(`${config.serviceUrls.account}/account`, {
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data ?? [];
}
