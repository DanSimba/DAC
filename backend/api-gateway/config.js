import 'dotenv/config';

export const config = Object.freeze({
  port: Number(process.env.PORT),
  frontendUrl: process.env.FRONTEND_URL,
  jwtSecret: process.env.JWT_SECRET,
  jwtExpiration: '8h',
  sessionTtlSeconds: 1800,
  serviceUrls: {
    auth: process.env.MS_AUTH_URL,
    account: process.env.MS_ACCOUNT_URL,
    cliente: process.env.MS_CLIENTE_URL,
    gerente: process.env.MS_GERENTE_URL
  },
  redisKeys: {
    session: 'sessao:',
    sessionByCpf: 'sessao:cpf:',
    revokedToken: 'token:revogado:'
  }
});
