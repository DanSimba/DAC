import cors from 'cors';
import express from 'express';
import { config } from './config.js';
import { rabbitMQ } from './infra/rabbitmq.js';
import { redis } from './infra/redis.js';
import { createRoutes } from './routes.js';
import * as clienteService from './services/cliente.service.js';
import * as contaService from './services/conta.service.js';
import * as gerenteService from './services/gerente.service.js';
import { reiniciar } from './services/reboot.service.js';
import * as solicitacaoService from './services/solicitacao.service.js';

const app = express();
const port = config.port || 8080;
let server;
let shuttingDown = false;

app.use(cors({
  origin: config.frontendUrl ?? 'http://localhost:4200',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: [
    'Content-Type',
    'Authorization',
    'x-access-token',
    'x-user-cpf',
    'x-user-tipo'
  ]
}));
app.use(express.json());

app.get('/', (req, res) => {
  return res.status(200).send('Olá Mundo!');
});

app.get('/health', (req, res) => {
  return res.status(200).json({ status: 'UP' });
});

app.post('/reboot', async (req, res) => {
  const resultado = await reiniciar();

  return res.status(200).json(resultado);
});

app.use(createRoutes({
  solicitacaoService,
  gerenteService,
  clienteService,
  contaService
}));

async function start() {
  await Promise.all([
    redis.initialize(),
    rabbitMQ.initialize()
  ]);

  server = app.listen(port, () => {
    console.log(`API Gateway rodando na porta ${port}.`);
  });
}

async function shutdown(signal) {
  if (shuttingDown) {
    return;
  }

  shuttingDown = true;
  console.log(`Encerrando API Gateway por ${signal}.`);

  if (server) {
    await new Promise((resolve, reject) => {
      server.close(error => {
        if (error) {
          reject(error);
          return;
        }

        resolve();
      });
    });
  }

  await Promise.allSettled([
    rabbitMQ.close(),
    redis.close()
  ]);
}

process.once('SIGINT', () => {
  shutdown('SIGINT')
    .then(() => process.exit(0))
    .catch(error => {
      console.error('Erro ao encerrar o API Gateway:', error);
      process.exit(1);
    });
});

process.once('SIGTERM', () => {
  shutdown('SIGTERM')
    .then(() => process.exit(0))
    .catch(error => {
      console.error('Erro ao encerrar o API Gateway:', error);
      process.exit(1);
    });
});

start().catch(async error => {
  console.error('Falha ao iniciar o API Gateway:', error);
  await Promise.allSettled([
    rabbitMQ.close(),
    redis.close()
  ]);
  process.exit(1);
});
