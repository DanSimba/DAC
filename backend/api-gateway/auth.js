import jwt from 'jsonwebtoken';
import { config } from './config.js';
import { redis } from './infra/redis.js';

function getToken(req) {
  const accessToken = req.get('x-access-token');

  if (accessToken) {
    return accessToken;
  }

  const authorization = req.get('authorization');

  if (authorization?.startsWith('Bearer ')) {
    return authorization.slice(7);
  }

  return undefined;
}

function unauthorized(res, message) {
  return res.status(401).json({
    auth: false,
    message
  });
}

export async function autenticar(req, res, next) {
  const token = getToken(req);

  if (!token) {
    return unauthorized(res, 'Token não fornecido.');
  }

  let usuario;

  try {
    usuario = jwt.verify(token, config.jwtSecret);

    if (
      typeof usuario !== 'object' ||
      !usuario.jti ||
      !usuario.cpf ||
      !usuario.tipo
    ) {
      return unauthorized(res, 'Falha ao autenticar o token.');
    }
  } catch {
    return unauthorized(res, 'Falha ao autenticar o token.');
  }

  try {
    const redisClient = await redis.getClient();
    const sessionKey = `${config.redisKeys.session}${usuario.jti}`;
    const sessionByCpfKey = `${config.redisKeys.sessionByCpf}${usuario.cpf}`;
    const revokedTokenKey = `${config.redisKeys.revokedToken}${usuario.jti}`;

    const [session, revokedToken] = await Promise.all([
      redisClient.get(sessionKey),
      redisClient.get(revokedTokenKey)
    ]);

    if (
      !session ||
      revokedToken ||
      session.toUpperCase() !== usuario.tipo.toUpperCase()
    ) {
      return unauthorized(res, 'Falha ao autenticar o token.');
    }

    await Promise.all([
      redisClient.expire(sessionKey, config.sessionTtlSeconds),
      redisClient.expire(sessionByCpfKey, config.sessionTtlSeconds)
    ]);

    req.usuario = {
      cpf: usuario.cpf,
      tipo: usuario.tipo,
      jti: usuario.jti,
      exp: usuario.exp
    };

    req.headers['x-user-cpf'] = String(usuario.cpf);
    req.headers['x-user-tipo'] = String(usuario.tipo);

    return next(); //Padrão de express, manda ele passar para o próximo middleware
  } catch (error) {
    console.error('Erro ao validar sessão no Redis:', error);

    return res.status(503).json({
      message: 'Serviço de autenticação temporariamente indisponível.'
    });
  }
}

export function autorizar(perfilPermitido) {
  return function verificarPerfil(req, res, next) {
    const perfilUsuario =
      req.usuario?.tipo?.toUpperCase();

    if (
      perfilUsuario !== perfilPermitido.toUpperCase()
    ) {
      return res.status(403).json({
        message: 'Acesso negado.'
      });
    }

    return next();
  };
}
