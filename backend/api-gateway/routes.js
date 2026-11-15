import express from 'express';

export function createRoutes({
  gatewayService,
  authService,
  solicitacaoService,
  sagaService,
  gerenteService,
  clienteService,
  contaService,
  autenticar,
  autorizar
}) {
  const router = express.Router();
  const somenteGerente = [autenticar, autorizar('GERENTE')];
  const somenteCliente = [autenticar, autorizar('CLIENTE')];

  router.get('/', gatewayService.status);


  //auth
  router.post('/auth/login', authService.login);
  router.post('/auth/logout', autenticar, authService.logout);
  router.get('/validacao', autenticar, authService.validar);


  //solicitacao
  router.post('/solicitacoes', solicitacaoService.criar);
  router.get('/solicitacoes', ...somenteGerente, solicitacaoService.listar);
  router.get('/solicitacoes/:cpf', ...somenteGerente, solicitacaoService.buscarPorCpf);
  router.post('/solicitacoes/:cpf/rejeicao', ...somenteGerente, solicitacaoService.rejeitar);
  router.post('/solicitacoes/:cpf/aprovacao', ...somenteGerente, solicitacaoService.aprovar);


  //gerente
  router.get('/gerentes', ...somenteGerente, gerenteService.listar);
  router.get('/gerentes/:identificador', ...somenteGerente, gerenteService.buscar);
  router.post('/gerentes', ...somenteGerente, gerenteService.criar);
  router.put('/gerentes/:id', ...somenteGerente, gerenteService.atualizar);
  router.delete('/gerentes/:id', ...somenteGerente, gerenteService.remover);

  // jobs / saga
  router.get('/jobs/:jobId/status', ...somenteGerente, autenticar, sagaService.buscarStatus);
  router.get('/jobs/:jobId/result', ...somenteGerente, autenticar, sagaService.buscarResultado);

  //clientes
  router.get('/clientes', ...somenteGerente, clienteService.listar);
  router.get('/clientes/findNameByCpf/:cpf', ...somenteGerente, clienteService.buscarPorCpf);
  router.get('/clientes/findByCpf/:cpf', ...somenteGerente, clienteService.buscarPorCpf);
  router.get('/clientes/:cpf', ...somenteGerente, clienteService.buscarPorCpf );
  router.get('/relatorio-clientes', ...somenteGerente, clienteService.gerarRelatorio);


  //conta
  router.get('/account/findByCpf/:cpf', ...somenteCliente, contaService.buscarPorCpf);
  router.get('/account/findByNumber/:number', ...somenteCliente, contaService.buscarPorNumero);
  router.post('/account/transfer', ...somenteCliente, contaService.transferir);
  router.post('/account/operate', ...somenteCliente, contaService.operar);

  return router;
}
