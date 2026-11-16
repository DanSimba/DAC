import { randomUUID } from 'node:crypto';
import jwt from 'jsonwebtoken';
import { config } from '../config.js';
import { HttpError, request } from '../infra/http-client.js';
import { redis } from '../infra/redis.js';

export class AuthServiceError extends Error {
  constructor(status, message) {
    super(message);
    this.name = 'AuthServiceError';
    this.status = status;
  }
}

async function buscarUsuario(cpf, tipo) {
  const cpfEncoded = encodeURIComponent(cpf);

  if (tipo === 'CLIENTE') {
    const { data } = await request(
      `${config.serviceUrls.cliente}/clientes/findByCpf/${cpfEncoded}`,
      {
        unavailableMessage: 'Erro ao buscar os dados do usuário.'
      }
    );

    return data;
  }

  if (tipo === 'GERENTE') {
    const { data } = await request(
      `${config.serviceUrls.gerente}/gerente`,
      {
        unavailableMessage: 'Erro ao buscar os dados do usuário.'
      }
    );
    const gerente = data.find(item => String(item.cpf) === String(cpf));

    if (!gerente) {
      throw new AuthServiceError(404, 'Gerente não encontrado.');
    }

    return gerente;
  }

  throw new AuthServiceError(502, 'Tipo de usuário inválido.');
}

export async function login(email, senha) {
  if (!email || !senha) {
    throw new AuthServiceError(400, 'E-mail e senha são obrigatórios.');
  }

  let authData;

  try {
    const response = await request(
      `${config.serviceUrls.auth}/auth/login`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, senha }),
        unavailableMessage: 'Erro ao comunicar com o serviço de autenticação.'
      }
    );

    authData = response.data;
  } catch (error) {
    if (error instanceof HttpError && error.status === 401) {
      throw new AuthServiceError(401, 'Login inválido!');
    }

    throw error;
  }

  if (!authData?.cpf || !authData?.tipo) {
    throw new AuthServiceError(502, 'Resposta inválida do serviço de autenticação.');
  }

  const tipo = authData.tipo.toUpperCase();
  const usuarioData = await buscarUsuario(authData.cpf, tipo);
  const jti = randomUUID();
  const token = jwt.sign(
    {
      cpf: authData.cpf,
      tipo
    },
    config.jwtSecret,
    {
      expiresIn: config.jwtExpiration,
      jwtid: jti
    }
  );

  try {
    const redisClient = await redis.getClient();

    await Promise.all([
      redisClient.set(
        `${config.redisKeys.session}${jti}`,
        tipo,
        { EX: config.sessionTtlSeconds }
      ),
      redisClient.set(
        `${config.redisKeys.sessionByCpf}${authData.cpf}`,
        jti,
        { EX: config.sessionTtlSeconds }
      )
    ]);
  } catch {
    throw new AuthServiceError(503, 'Redis indisponível.');
  }

  return {
    auth: true,
    token,
    tipo,
    usuario: {
      cpf: authData.cpf,
      nome: usuarioData.nome,
      email: usuarioData.email ?? email
    }
  };
}

export async function logout({ cpf, jti, exp }) {
  if (!cpf || !jti || !exp) {
    throw new AuthServiceError(400, 'Dados da sessão não informados.');
  }

  const sessionKey = `${config.redisKeys.session}${jti}`;
  const sessionByCpfKey = `${config.redisKeys.sessionByCpf}${cpf}`;
  const revokedTokenKey = `${config.redisKeys.revokedToken}${jti}`;
  const remainingTokenTtl = Math.max(
    exp - Math.floor(Date.now() / 1000),
    0
  );

  try {
    const redisClient = await redis.getClient();
    await redisClient.del([sessionKey, sessionByCpfKey]);

    if (remainingTokenTtl > 0) {
      await redisClient.set(
        revokedTokenKey,
        '1',
        { EX: remainingTokenTtl }
      );
    }
  } catch {
    throw new AuthServiceError(503, 'Redis indisponível.');
  }

  return {
    auth: false,
    message: 'Logout realizado com sucesso.'
  };
}
