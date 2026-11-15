import { config } from '../config.js';
import { request } from '../infra/http-client.js';

export async function buscarContas() {
  const { data } = await request(`${config.serviceUrls.account}/account`, {
    unavailableMessage: 'MS Conta indisponível.'
  });

  return data ?? [];
}

export async function buscarPorCpf(req, res) {
  const { data } = await request(
    `${config.serviceUrls.account}/account/findByCpf/${encodeURIComponent(req.params.cpf)}`,
    { unavailableMessage: 'MS Conta indisponível.' }
  );

  return res.status(200).json(data);
}

export async function buscarPorNumero(req, res) {
  const { data } = await request(
    `${config.serviceUrls.account}/account/findByNumber/${encodeURIComponent(req.params.number)}`,
    { unavailableMessage: 'MS Conta indisponível.' }
  );

  return res.status(200).json(data);
}

export async function buscarUltimaVersao(req, res) {
  const { data } = await request(
    `${config.serviceUrls.account}/account/getLastVersion/${encodeURIComponent(req.params.number)}`,
    { unavailableMessage: 'MS Conta indisponível.' }
  );

  return res.status(200).json(data);
}