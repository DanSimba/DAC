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
  router.get('/validacao', autenticar, authService.validar);


  //solicitacao
  router.post('/solicitacoes', solicitacaoService.criar);
  router.get('/solicitacoes', ...somenteGerente, solicitacaoService.listar);
  router.get('/solicitacoes/:cpf', ...somenteGerente, solicitacaoService.buscarPorCpf);
  router.post('/solicitacoes/:cpf/rejeicao', ...somenteGerente, solicitacaoService.rejeitar);
  router.post('/solicitacoes/:cpf/aprovacao', ...somenteGerente, solicitacaoService.aprovar);


  //gerente
  router.get('/jobs/:jobId/status', ...somenteGerente, sagaService.buscarStatus);
  router.get('/gerentes', ...somenteGerente, gerenteService.listar);
  router.get('/gerentes/:identificador', ...somenteGerente, gerenteService.buscar);
  router.post('/gerentes', ...somenteGerente, gerenteService.criar);
  router.put('/gerentes/:id', ...somenteGerente, gerenteService.atualizar);
  router.delete('/gerentes/:id', ...somenteGerente, gerenteService.remover);

  //clientes
  router.get('/clientes', ...somenteGerente, clienteService.listar);
  router.get('/relatorio-clientes', ...somenteGerente, clienteService.gerarRelatorio);


  //conta
  router.post('/account/transfer', ...somenteCliente, contaService.transferir);
  router.post('/account/operate', ...somenteCliente, contaService.operar);

  return router;
}
