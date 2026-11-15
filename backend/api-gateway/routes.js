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
  router.post('/solicitacoes', async (req, res) => {
    const solicitacao = await solicitacaoService.criar(req.body);
    const cpf = encodeURIComponent(solicitacao.cpf);
    const resultado = addLinks(
      req,
      solicitacao,
      { self: `/solicitacoes/${cpf}` }
    );

    res.location(resultado._links.self.href);
    return res.status(201).json(resultado);
  });

  router.get('/solicitacoes', ...somenteGerente, async (req, res) => {
    const solicitacoes = await solicitacaoService.listar(req.query.status);
    const resultado = solicitacoes.map(solicitacao => {
      const cpf = encodeURIComponent(solicitacao.cpf);
      const links = { self: `/solicitacoes/${cpf}` };

      if (solicitacao.status === 'PENDENTE') {
        links.aprovar = {
          href: `/solicitacoes/${cpf}/aprovacao`,
          method: 'POST'
        };
        links.rejeitar = {
          href: `/solicitacoes/${cpf}/rejeicao`,
          method: 'POST'
        };
      }

      return addLinks(req, solicitacao, links);
    });

    return res.status(200).json(resultado);
  });

  router.get('/solicitacoes/:cpf', ...somenteGerente, async (req, res) => {
    const solicitacao = await solicitacaoService.buscarPorCpf(req.params.cpf);
    const cpf = encodeURIComponent(solicitacao.cpf);
    const links = {
      self: `/solicitacoes/${cpf}`,
      solicitacoes: '/solicitacoes'
    };

    if (solicitacao.status === 'PENDENTE') {
      links.aprovar = {
        href: `/solicitacoes/${cpf}/aprovacao`,
        method: 'POST'
      };
      links.rejeitar = {
        href: `/solicitacoes/${cpf}/rejeicao`,
        method: 'POST'
      };
    }

    return res.status(200).json(addLinks(req, solicitacao, links));
  });

  router.post('/solicitacoes/:cpf/rejeicao', ...somenteGerente, async (req, res) => {
    const solicitacao = await solicitacaoService.rejeitar(
      req.params.cpf,
      req.body
    );
    const cpf = encodeURIComponent(solicitacao.cpf);
    const resultado = addLinks(
      req,
      solicitacao,
      {
        self: `/solicitacoes/${cpf}`,
        solicitacoes: '/solicitacoes'
      }
    );

    return res.status(200).json(resultado);
  });

  router.post('/solicitacoes/:cpf/aprovacao', ...somenteGerente, async (req, res) => {
    const job = await solicitacaoService.aprovar(req.params.cpf);
    const resultado = addLinks(
      req,
      job,
      { status: `/jobs/${job.jobId}/status` }
    );

    return res.status(202).json(resultado);
  });


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
  router.get('/account/findByCpf/:cpf', ...somenteCliente, contaService.buscarPorCpf);
  router.get('/account/findByNumber/:number', ...somenteCliente, contaService.buscarPorNumero);
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
