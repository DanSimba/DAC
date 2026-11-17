import express from 'express';
import { autenticar, autorizar } from './auth.js';
import { addLinks } from './helpers/hateoas.js';
import { login, logout } from './services/auth.service.js';
import { buscarResultado, buscarStatus } from './services/saga.service.js';

export function createRoutes({
  solicitacaoService,
  gerenteService,
  clienteService,
  contaService
}) {
  const router = express.Router();
  const somenteGerente = [autenticar, autorizar('GERENTE')];
  const somenteCliente = [autenticar, autorizar('CLIENTE')];

  function jobAceito(req, res, job) {
    res.location(`/jobs/${job.jobId}/status`);
    return res.status(202).json(job);
  }

  function normalizarSolicitacao(solicitacao) {
    const status = solicitacao.status === 'APROVADO'
      ? 'APROVADA'
      : solicitacao.status === 'NAO_APROVADO'
        ? 'NAO_APROVADA'
        : solicitacao.status;

    return { ...solicitacao, status };
  }

  async function executarLogin(req, res) {
    const { email, senha } = req.body ?? {};
    const resultado = await login(email, senha);
    return res.status(200).json(resultado);
  }

  async function executarLogout(req, res) {
    await logout(req.usuario);
    return res.status(204).end();
  }

  //auth
  router.post('/login', executarLogin);
  router.post('/auth/login', executarLogin);

  router.post('/logout', autenticar, executarLogout);
  router.post('/auth/logout', autenticar, executarLogout);

  router.get('/validacao', autenticar, (req, res) => {
    return res.status(200).json({
      message: 'AUTORIZADO',
      usuario: req.usuario
    });
  });


  //solicitacao
  router.post('/solicitacoes', async (req, res) => {
    const solicitacao = normalizarSolicitacao(
      await solicitacaoService.criar(req.body)
    );
    const cpf = encodeURIComponent(solicitacao.cpf);
    const resultado = addLinks(req, solicitacao, {
      self: `/solicitacoes/${cpf}`,
      aprovacao: {
        href: `/solicitacoes/${cpf}/aprovacao`,
        method: 'POST'
      },
      rejeicao: {
        href: `/solicitacoes/${cpf}/rejeicao`,
        method: 'POST'
      }
    });

    res.location(resultado._links.self.href);
    return res.status(201).json(resultado);
  });

  router.get('/solicitacoes', ...somenteGerente, async (req, res) => {
    const solicitacoes = await solicitacaoService.listar(req.query.status);
    const resultado = solicitacoes.map(item => {
      const solicitacao = normalizarSolicitacao(item);
      const cpf = encodeURIComponent(solicitacao.cpf);
      const links = { self: `/solicitacoes/${cpf}` };

      if (solicitacao.status === 'PENDENTE') {
        links.aprovacao = {
          href: `/solicitacoes/${cpf}/aprovacao`,
          method: 'POST'
        };
        links.rejeicao = {
          href: `/solicitacoes/${cpf}/rejeicao`,
          method: 'POST'
        };
      }

      return addLinks(req, solicitacao, links);
    });

    return res.status(200).json(addLinks(
      req,
      { solicitacoes: resultado },
      { self: '/solicitacoes' }
    ));
  });

  router.get('/solicitacoes/:cpf', ...somenteGerente, async (req, res) => {
    const solicitacao = normalizarSolicitacao(
      await solicitacaoService.buscarPorCpf(req.params.cpf)
    );
    const cpf = encodeURIComponent(solicitacao.cpf);
    const links = {
      self: `/solicitacoes/${cpf}`,
      solicitacoes: '/solicitacoes'
    };

    if (solicitacao.status === 'PENDENTE') {
      links.aprovacao = {
        href: `/solicitacoes/${cpf}/aprovacao`,
        method: 'POST'
      };
      links.rejeicao = {
        href: `/solicitacoes/${cpf}/rejeicao`,
        method: 'POST'
      };
    }

    return res.status(200).json(addLinks(req, solicitacao, links));
  });

  router.post('/solicitacoes/:cpf/rejeicao', ...somenteGerente, async (req, res) => {
    const solicitacao = normalizarSolicitacao(await solicitacaoService.rejeitar(
      req.params.cpf,
      req.body
    ));
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
    return jobAceito(req, res, job);
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
    return res.status(200).json(status);
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

  router.get('/gerentes', ...somenteGerente, async (req, res) => {
    const gerentes = await gerenteService.listar();
    const resultado = gerentes.map(gerente => addLinks(
      req,
      gerente,
      {
        self: `/gerentes/${gerente.cpf}`,
        atualizar: {
          href: `/gerentes/${gerente.cpf}`,
          method: 'PUT'
        },
        remover: {
          href: `/gerentes/${gerente.cpf}`,
          method: 'DELETE'
        }
      }
    ));

    return res.status(200).json(addLinks(
      req,
      { gerentes: resultado },
      { self: '/gerentes', cadastro: { href: '/gerentes', method: 'POST' } }
    ));
  });

  router.get('/gerentes/:identificador', ...somenteGerente, async (req, res) => {
    const gerente = await gerenteService.buscar(req.params.identificador);
    const resultado = addLinks(
      req,
      gerente,
      {
        self: `/gerentes/${gerente.cpf}`,
        gerentes: '/gerentes',
        atualizar: {
          href: `/gerentes/${gerente.cpf}`,
          method: 'PUT'
        },
        remover: {
          href: `/gerentes/${gerente.cpf}`,
          method: 'DELETE'
        }
      }
    );

    return res.status(200).json(resultado);
  });

  router.post('/gerentes', ...somenteGerente, async (req, res) => {
    const job = await gerenteService.criar(req.body);
    return jobAceito(req, res, job);
  });

  router.put('/gerentes/:id', ...somenteGerente, async (req, res) => {
    const gerente = await gerenteService.atualizar(req.params.id, req.body);
    const resultado = addLinks(
      req,
      gerente,
      {
        self: `/gerentes/${gerente.cpf}`,
        gerentes: '/gerentes',
        remover: {
          href: `/gerentes/${gerente.cpf}`,
          method: 'DELETE'
        }
      }
    );

    return res.status(200).json(resultado);
  });

  router.delete('/gerentes/:id', ...somenteGerente, async (req, res) => {
    const job = await gerenteService.remover(
      req.params.id,
      req.usuario.cpf
    );
    return jobAceito(req, res, job);
  });

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

    return res.status(200).json(addLinks(
      req,
      { clientes: resultado },
      { self: '/clientes' }
    ));
  });

  router.get('/clientes/:cpf', autenticar, async (req, res) => {
    if (
      req.usuario.tipo.toUpperCase() === 'CLIENTE'
      && String(req.usuario.cpf) !== String(req.params.cpf)
    ) {
      return res.status(403).json({ message: 'Acesso negado.' });
    }
    const cliente = await clienteService.buscarPorCpf(req.params.cpf);
    const resultado = addLinks(
      req,
      cliente,
      {
        self: `/clientes/${cliente.cpf}`,
        clientes: '/clientes',
        conta: `/clientes/${encodeURIComponent(cliente.cpf)}/conta`
      }
    );

    return res.status(200).json(resultado);
  });

  router.get('/relatorio-clientes', ...somenteGerente, async (req, res) => {
    const job = await clienteService.gerarRelatorio();
    return jobAceito(req, res, job);
  });

  router.get('/relatorios/clientes', ...somenteGerente, async (req, res) => {
    const job = await clienteService.gerarRelatorio();
    return jobAceito(req, res, job);
  });

  router.get('/clientes/:cpf/conta', autenticar, async (req, res) => {
    if (
      req.usuario.tipo.toUpperCase() === 'CLIENTE'
      && String(req.usuario.cpf) !== String(req.params.cpf)
    ) {
      return res.status(403).json({ message: 'Acesso negado.' });
    }

    const conta = await contaService.buscarPorCpf(req.params.cpf, req.usuario);
    const publica = contaService.paraContratoPublico(conta);
    return res.status(200).json(addLinks(req, publica, {
      self: `/contas/${publica.numero}`,
      cliente: `/clientes/${publica.cpfCliente}`,
      deposito: { href: `/contas/${publica.numero}/deposito`, method: 'POST' },
      saque: { href: `/contas/${publica.numero}/saque`, method: 'POST' },
      transferencia: { href: `/contas/${publica.numero}/transferencia`, method: 'POST' },
      extrato: `/contas/${publica.numero}/extrato`
    }));
  });


  //conta
  router.get('/contas/:numero', autenticar, async (req, res) => {
    const conta = await contaService.buscarPorNumero(req.params.numero, req.usuario);

    if (
      req.usuario.tipo.toUpperCase() === 'CLIENTE'
      && String(conta.client_cpf) !== String(req.usuario.cpf)
    ) {
      return res.status(403).json({ message: 'Acesso negado.' });
    }

    const publica = contaService.paraContratoPublico(conta);
    return res.status(200).json(addLinks(req, publica, {
      self: `/contas/${publica.numero}`,
      cliente: `/clientes/${publica.cpfCliente}`,
      deposito: { href: `/contas/${publica.numero}/deposito`, method: 'POST' },
      saque: { href: `/contas/${publica.numero}/saque`, method: 'POST' },
      transferencia: { href: `/contas/${publica.numero}/transferencia`, method: 'POST' },
      extrato: `/contas/${publica.numero}/extrato`
    }));
  });

  router.post('/contas/:numero/deposito', ...somenteCliente, async (req, res) => {
    const resultado = await contaService.realizarOperacaoPublica(
      'DEPOSITO', req.params.numero, req.body, req.usuario
    );
    return res.status(201).json(addLinks(req, resultado, {
      conta: `/contas/${req.params.numero}`,
      extrato: `/contas/${req.params.numero}/extrato`
    }));
  });

  router.post('/contas/:numero/saque', ...somenteCliente, async (req, res) => {
    const resultado = await contaService.realizarOperacaoPublica(
      'SAQUE', req.params.numero, req.body, req.usuario
    );
    return res.status(201).json(addLinks(req, resultado, {
      conta: `/contas/${req.params.numero}`,
      extrato: `/contas/${req.params.numero}/extrato`
    }));
  });

  router.post('/contas/:numero/transferencia', ...somenteCliente, async (req, res) => {
    const resultado = await contaService.transferirPublico(
      req.params.numero, req.body, req.usuario
    );
    return res.status(201).json(addLinks(req, resultado, {
      conta: `/contas/${req.params.numero}`,
      extrato: `/contas/${req.params.numero}/extrato`
    }));
  });

  router.get('/contas/:numero/extrato', autenticar, async (req, res) => {
    const conta = await contaService.buscarPorNumero(req.params.numero, req.usuario);
    if (
      req.usuario.tipo.toUpperCase() === 'CLIENTE'
      && String(conta.client_cpf) !== String(req.usuario.cpf)
    ) {
      return res.status(403).json({ message: 'Acesso negado.' });
    }
    const extrato = await contaService.buscarExtrato(
      req.params.numero, req.usuario, req.query
    );
    return res.status(200).json(addLinks(req, extrato, {
      self: `/contas/${req.params.numero}/extrato`,
      conta: `/contas/${req.params.numero}`
    }));
  });
  router.get('/account/findByCpf/:cpf', autenticar, async (req, res) => {
    if (
      req.usuario.tipo.toUpperCase() === 'CLIENTE'
      && String(req.usuario.cpf) !== String(req.params.cpf)
    ) {
      return res.status(403).json({ message: 'Acesso negado.' });
    }

    const conta = await contaService.buscarPorCpf(req.params.cpf, req.usuario);
    const numero = encodeURIComponent(conta.number);
    const links = req.usuario.tipo.toUpperCase() === 'GERENTE'
      ? { self: `/account/findByCpf/${encodeURIComponent(conta.client_cpf)}` }
      : {
          self: `/account/findByNumber/${numero}`,
          extrato: `/account/listExtratos/${numero}`,
          ultimaVersao: `/account/getLastVersion/${numero}`,
          depositar: { href: '/account/depositar', method: 'POST' },
          sacar: { href: '/account/sacar', method: 'POST' },
          transferir: { href: '/account/transfer', method: 'POST' }
        };

    return res.status(200).json(addLinks(req, conta, links));
  });

  router.get('/account/findByNumber/:number', ...somenteCliente, async (req, res) => {
    const conta = await contaService.buscarPorNumero(
      req.params.number,
      req.usuario
    );
    const numero = encodeURIComponent(conta.number);
    const resultado = addLinks(
      req,
      conta,
      {
        self: `/account/findByNumber/${numero}`,
        extrato: `/account/listExtratos/${numero}`,
        ultimaVersao: `/account/getLastVersion/${numero}`,
        depositar: { href: '/account/depositar', method: 'POST' },
        sacar: { href: '/account/sacar', method: 'POST' },
        transferir: { href: '/account/transfer', method: 'POST' }
      }
    );

    return res.status(200).json(resultado);
  });

  router.get('/account/listExtratos/:number', ...somenteCliente, async (req, res) => {
    const extratos = await contaService.listarExtratos(
      req.params.number,
      req.usuario
    );

    return res.status(200).json(extratos);
  });

  router.get('/account/getLastVersion/:number', ...somenteCliente, async (req, res) => {
    const versao = await contaService.buscarUltimaVersao(
      req.params.number,
      req.usuario
    );

    return res.status(200).json(versao);
  });

  router.post('/account/depositar', ...somenteCliente, async (req, res) => {
    const resultado = await contaService.depositar(req.body, req.usuario);

    return resultado === undefined
      ? res.status(200).end()
      : res.status(200).json(resultado);
  });

  router.post('/account/sacar', ...somenteCliente, async (req, res) => {
    const resultado = await contaService.sacar(req.body, req.usuario);

    return resultado === undefined
      ? res.status(200).end()
      : res.status(200).json(resultado);
  });

  router.post('/account/transfer', ...somenteCliente, async (req, res) => {
    const resultado = await contaService.transferir(req.body, req.usuario);

    return resultado === undefined
      ? res.status(200).end()
      : res.status(200).json(resultado);
  });

  router.use((error, req, res, next) => {
    if (res.headersSent) {
      return next(error);
    }

    const status = error.status ?? 500;
    const message = status === 500
      ? 'Erro interno do Gateway.'
      : error.message;
    const body = {
      status,
      erro: status === 500 ? 'Internal Server Error' : 'Erro',
      mensagem: message
    };

    if (status === 401) {
      body.auth = false;
      body.message = message;
    }

    return res.status(status).json(body);
  });

  return router;
}
