import express from 'express';
import { autenticar, autorizar } from './auth.js';
import { addLinks } from './helpers/hateoas.js';
import { login, logout } from './services/auth.service.js';
import { buscarResultado, buscarStatus } from './services/saga.service.js';

export function createRoutes({
  gatewayService,
  solicitacaoService,
  gerenteService,
  clienteService,
  contaService
}) {
  const router = express.Router();
  const somenteGerente = [autenticar, autorizar('GERENTE')];
  const somenteCliente = [autenticar, autorizar('CLIENTE')];

  router.get('/', gatewayService.status);


  //auth
  router.post('/auth/login', async (req, res) => {
    const { email, senha } = req.body ?? {};
    const resultado = await login(email, senha);

    return res.status(200).json(resultado);
  });

  router.post('/auth/logout', autenticar, async (req, res) => {
    const resultado = await logout(req.usuario);

    return res.status(200).json(resultado);
  });

  router.get('/validacao', autenticar, (req, res) => {
    return res.status(200).json({
      message: 'AUTORIZADO',
      usuario: req.usuario
    });
  });


  //solicitacao
  router.post('/solicitacoes', solicitacaoService.criar);
  router.get('/solicitacoes', ...somenteGerente, solicitacaoService.listar);
  router.get('/solicitacoes/:cpf', ...somenteGerente, solicitacaoService.buscarPorCpf);
  router.post('/solicitacoes/:cpf/rejeicao', ...somenteGerente, solicitacaoService.rejeitar);
  router.post('/solicitacoes/:cpf/aprovacao', ...somenteGerente, solicitacaoService.aprovar);


  //gerente
  router.get('/jobs/:jobId/status', ...somenteGerente, async (req, res) => {
    const job = await buscarStatus(req.params.jobId);

    if (!job) {
      return res.status(404).json({
        message: 'Job não encontrado ou expirado.'
      });
    }

    const { resultado, ...status } = job;
    const links = {
      self: `/jobs/${job.jobId}/status`
    };

    if (job.status === 'CONCLUIDO' && job.resultType === 'inline') {
      links.resultado = `/jobs/${job.jobId}/result`;
    }

    if (
      job.status === 'CONCLUIDO'
      && job.resultType === 'resource'
      && job.dominio
      && job.resourceId
    ) {
      links.recurso = `/${job.dominio}/${encodeURIComponent(job.resourceId)}`;
    }

    return res.status(200).json(addLinks(req, status, links));
  });

  router.get('/jobs/:jobId/result', ...somenteGerente, async (req, res) => {
    const resultado = await buscarResultado(req.params.jobId);

    if (resultado === null || resultado === undefined) {
      return res.status(404).json({
        message: 'Resultado não disponível.'
      });
    }

    return res.status(200).json(resultado);
  });

  router.get('/gerentes', ...somenteGerente, gerenteService.listar);
  router.get('/gerentes/:identificador', ...somenteGerente, gerenteService.buscar);
  router.post('/gerentes', ...somenteGerente, gerenteService.criar);
  router.put('/gerentes/:id', ...somenteGerente, gerenteService.atualizar);
  router.delete('/gerentes/:id', ...somenteGerente, gerenteService.remover);

  //clientes
  router.get('/clientes', ...somenteGerente, async (req, res) => {
    const clientes = await clienteService.listar(req.query);
    const resultado = clientes.map(cliente => addLinks(
      req,
      cliente,
      {
        self: `/clientes/${cliente.cpf}`,
        conta: `/account/findByCpf/${encodeURIComponent(cliente.cpf)}`
      }
    ));

    return res.status(200).json(resultado);
  });

  router.get('/clientes/:cpf', ...somenteGerente, async (req, res) => {
    const cliente = await clienteService.buscarPorCpf(req.params.cpf);
    const resultado = addLinks(
      req,
      cliente,
      {
        self: `/clientes/${cliente.cpf}`,
        clientes: '/clientes',
        conta: `/account/findByCpf/${encodeURIComponent(cliente.cpf)}`
      }
    );

    return res.status(200).json(resultado);
  });

  router.get('/relatorio-clientes', ...somenteGerente, async (req, res) => {
    const job = await clienteService.gerarRelatorio();
    const resultado = addLinks(
      req,
      job,
      { status: `/jobs/${job.jobId}/status` }
    );

    return res.status(202).json(resultado);
  });


  //conta
  router.post('/account/transfer', ...somenteCliente, contaService.transferir);
  router.post('/account/operate', ...somenteCliente, contaService.operar);

  router.use((error, req, res, next) => {
    if (res.headersSent) {
      return next(error);
    }

    const status = error.status ?? 500;
    const message = status === 500
      ? 'Erro interno do Gateway.'
      : error.message;
    const body = { message };

    if (status === 401) {
      body.auth = false;
    }

    return res.status(status).json(body);
  });

  return router;
}
