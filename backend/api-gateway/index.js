const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const { timeStamp } = require('console');
const redis = require('redis');
const amqp = require('amqplib');
const { stat } = require('fs');

const app = express();
const port = 8080;

require('dotenv').config();

app.use(cors({
  origin: 'http://localhost:4200',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(express.json());

//pra tratar responses sem corpo e encaminhar status code: 
async function encaminhaResponse(response, res) {
  const texto = await response.text();
  if (!texto) {
    console.log("Status: ", res.status(response.status).end());
    return res.status(response.status).end();
  }
  try {
    console.log("Texto: ", res.status(response.status).json(JSON.parse(texto)));
    return res.status(response.status).json(JSON.parse(texto));
  } catch {
    console.error("Texto: ", res.status(response.status).send(texto));
    return res.status(response.status).send(texto);
  }
}

let redisClient;
let rabbitmqChannel;
let rabbitMqConnection;

async function iniciarServidor() {
  try {
    console.log("Conectando infra do API-Gateway");

    console.log("Conectando ao Redis...")
    redisClient = redis.createClient({ url : process.env.REDIS_URL });
    await redisClient.connect();
    console.log("Conectado ao Redis.")

    console.log("Conectando ao RabbitMQ...")
    //let rabbitMqConnection;
    let tentativas = 10;

    while (tentativas > 0) {
      try {
        rabbitMqConnection = await amqp.connect(process.env.RABBITMQ_URL);
        rabbitmqChannel = await rabbitMqConnection.createChannel();
        break;
      } catch (err) {
        tentativas--;
        console.log(`RabbitMQ indisponível, tentando novamente em 3 segundos... (${tentativas} tentativas restantes)`);
        await new Promise(resolve => setTimeout(resolve, 3000));
      }
    }

    if (!rabbitMqConnection) {
      throw new Error("Não foi possível conectar ao RabbitMQ após várias tentativas.");
    }

    app.listen(port, () => {
      console.log(`Servidor rodando em http://localhost:${port}`);
    });
  
  } catch (error) {
    console.error("Falha ao iniciar os serviços do API-Gateway: ", error);
    process.exit(1);
  }

};

  app.get('/', (req, res) => {
    res.send('Olá Mundo!');
  });


// Login
  app.post('/auth/login', async (req, res) => {
    console.log("Iniciando login...");
    const { email, senha } = req.body;
    console.log("req.body ", req.body);
    console.log("Email: " + email + " Senha: " + senha);

    if (!email || !senha) {
      console.error("E-mail e senha são obrigatórios");
      return res.status(400).json({
        status: 400,
        mensagem: 'E-mail e senha são obrigatórios.'
      });
    }

    try {
      console.log("Iniciando fetch POST para ms-auth");
      const urlAlvo = `${process.env.MS_AUTH_URL}/auth/login`;
      console.log(`Iniciando fetch POST para URL exata: ${urlAlvo}`)

      const authResponse = await fetch(urlAlvo, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha })
      });

      const responseText = await authResponse.text();
      console.log(`Status HTTP do ms-auth: ${authResponse.status}`);
      console.log(`Corpo da resposta do Java: '${responseText}'`);
      
      let data = {};
      if (responseText) {
        try {
          data = JSON.parse(responseText);
        } catch (e) {
          console.error("A resposta não é um JSON válido.");
        }
      }
      
      if (!authResponse.ok) {
        console.error("Erro no authResponse: ", data);
        return res.status(authResponse.status).json(data);
      }

      const usuarioInfo = data.usuario || data;
      console.log("usuarioInfo: ", usuarioInfo);
      
      let usuarioObj = { cpf: usuarioInfo.cpf, tipo: usuarioInfo.tipo };

      if (usuarioInfo.tipo === 'CLIENTE') {
        const clienteResponse = await fetch(`${process.env.MS_CLIENTE_URL}/clientes/findNameByCpf/${usuarioInfo.cpf}`);
        if (clienteResponse.ok) {
          const clienteData = await clienteResponse.json();
          usuarioObj.nome = clienteData.nome;
        }
      } else if (usuarioInfo.tipo === 'GERENTE') {
        const gerenteResponse = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${usuarioInfo.cpf}`);
        if (gerenteResponse.ok) {
          const gerenteData = await gerenteResponse.json();
          usuarioObj.nome = gerenteData.nome;
        }
      }

      const jti = crypto.randomUUID();
      console.log("jti: ", jti);

      const token = jwt.sign(
        {
          cpf: usuarioInfo.cpf,
          tipo: usuarioInfo.tipo
        },
        "MONSTERBANK",
        {
          expiresIn: "8h",
          jwtid: jti
        }
      );
      console.log("Token: ", token);

      const ttlSessao = 1800;
      await redisClient.set(`sessao:${jti}`, usuarioInfo.tipo, { EX: ttlSessao });
      await redisClient.set(`sessao:cpf:${usuarioInfo.cpf}`, jti, { EX: ttlSessao });

      return res.status(200).json({
        auth: true,
        token: token,
        tipo: usuarioInfo.tipo,
        usuario: usuarioObj
      });

    } catch (error) {
      console.error('Erro no login:', error.message);
      console.error('Causa raiz da falha:', error.cause);
      return res.status(500).json({
        status: 500,
        mensagem: 'Erro ao comunicar com o ms-auth.'
      });
    }
  });

// Valida Token

app.get('/validacao', (req, res) => {
  const authHeader = req.header("authorization");
  const token = authHeader && authHeader.split(" ")[1];

  if(!token) return res.status(403).json({error: "Não autorizado"});

  jwt.verify(token, "MONSTERBANK", (err, email) => {
    if(err) return res.status(403).json({error: "Não autorizado"});

    res.json({message: "AUTORIZADO", email});

  });

});

//função complementar pra validação do token
// function validaJWT(req, res, next){
//   const authHeader = req.headers.authorization;
//   if (!authHeader || !authHeader.startsWith('Bearer ')){
//     return res.status(401).json({
//       message: 'token não informado'
//     })
//   }
//   const token = authHeader.split(' ')[1];
//   try {
//     const usuario = jwt.verify(
//       token, 'MONSTERBANK'
//     )
//     req.usuario = usuario
//     return next()
//   } catch (error) {
//     return res.status(401).json({
//       message: 'token inválido ou expirado'
//     })
    
//   }

// }
    // TESTES
    function verificaToken(req, res, next) {
      console.log('Entrei para verificar token');

      const authHeader = req.headers.authorization;
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        console.error('Erro: Token não informado');
        return res.status(401).json({ message : 'Token não informado' });
      }

      const token = authHeader.split(' ')[1];
      console.log('Token: ', token);

      try {
        const usuario = jwt.verify(token, 'MONSTERBANK');
        req.usuario = usuario;
        console.log('Token existente e válido');
        return next();
      } catch (error) {
        console.error('Erro: Token inválido ou expirado');
        return res.status(401).json({ message : 'Token inválido ou expirado' });
      }
    }

    function verificaPerfil(perfil) {
      return (req, res, next) => {
        console.log('Entrei para verificar tipo de usuário');
        if (!req.usuario || !req.usuario.tipo) {
          console.error('Erro: Perfil não identificado');
          return res.status(403).json({ message : 'Acesso negado: Perfil não identificado' });
        }

        if (req.usuario.tipo.toUpperCase() !== perfil.toUpperCase()) {
          console.error('Erro: Sem permissão');
          return res.status(403).json({ message: 'Acesso negado: Você não tem permissão para esta ação' });
        }
        console.log('Tipo de usuário válido e com permissão');
        return next();
      }
    }


    // Encaminha o cliente recebido do front para o ms-cliente
    app.post('/solicitacoes', async (req, res) => {
      try {
        const response = await fetch(`${process.env.MS_CLIENTE_URL}/solicitacoes`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(req.body)
        });

        console.log("Entrei no Post para registrar uma solicitação.");

        return await encaminhaResponse(response, res);

      } catch (error) {
        console.error('Erro de rede ao acessar ms-cliente:', error.message);
        return res.status(503).json({
          status: 503,
          mensagem: 'Serviço indisponível.'
        });
      }
    });

    // Lista as solicitações
    app.get('/solicitacoes', verificaToken, verificaPerfil('GERENTE'), async (req, res) => {
      try {
        console.log("Entrei no Get para listar todas as solicitações.");
        const queryParams = new URLSearchParams(req.query).toString();
        const url = `${process.env.MS_CLIENTE_URL}/solicitacoes${queryParams ? '?' + queryParams : ''}`;
        const response = await fetch(url);

        return await encaminhaResponse(response, res);

      } catch (error) {
        console.error('Erro ao listar solicitação. ', error);
        return res.status(503).json({
          status: 503,
          mensagem: 'Serviço indisponível.'
        })
      }
    });

  // Listar solicitações por cpf
  app.get('/solicitacoes/:cpf', verificaToken, verificaPerfil('GERENTE'), async (req, res) => {
    try {
      console.log("Entrei no Get para listar uma solicitação");
      const url = `${process.env.MS_CLIENTE_URL}/solicitacoes/${req.params.cpf}`;
      const response = await fetch(url);
      
      return await encaminhaResponse(response, res);

    } catch (error) {
        console.error('Erro de rede ao acessar ms-cliente:', error.message);
        return res.status(503).json({
            status: 503,
            mensagem: 'Serviço indisponível.'
        });
      }
    });

    // Rejeitar solicitações
    app.post('/solicitacoes/:cpf/rejeicao', verificaToken, verificaPerfil('GERENTE'), async (req, res) => {
      try {
        console.log("Entrei no Post para rejeitar uma solicitação.");
        const response = await fetch(`${process.env.MS_CLIENTE_URL}/solicitacoes/${req.params.cpf}/rejeicao`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(req.body)
        });

        return await encaminhaResponse(response, res);

      } catch (error) {
        console.error(`Erro de rede ao rejeitar CPF ${req.params.cpf}:`, error.message);
        return res.status(503).json({
          status: 503, 
          mensagem: 'Serviço indisponível.' 
        });
      }
    });


  // Aprovar solicitações
  app.post('/solicitacoes/:cpf/aprovacao', verificaToken, verificaPerfil('GERENTE'), async (req, res) => {
    try {
      console.log("Entrei no Post para aprovar uma solicitação.");

      const cpf = req.params.cpf;
      const sagaId = crypto.randomUUID();

      const jobEstado = {
        jobId: sagaId,
        status: "PENDENTE",
        resultType: null,
        dominio: "clientes",
        resourceId: cpf,
        erro: null
      };

      await redisClient.set(
        `job:${sagaId}`,
        JSON.stringify(jobEstado),
        { EX: 300 }
      );

      const sagaMessage = {
        sagaId: sagaId,
        tipo: "aprovar-cliente",
        timestamp: new Date().toISOString(),
        payload: { cpf: cpf }
      };

      rabbitmqChannel.sendToQueue(
        'saga.cmd',
        Buffer.from(JSON.stringify(sagaMessage))
      );

      return res.status(202).json({ jobId: sagaId });

    } catch (error) {
      console.error(
        `Erro ao aprovar solicitação do CPF ${req.params.cpf}:`,
        error.message
      );

      return res.status(503).json({
        status: 503,
        mensagem: 'Serviço indisponível.'
      });
    }
  });

  // Rota de Polling da SAGA (R9, R13, R15, R16)
  app.get('/jobs/:jobId/status', async (req, res) => {
    try {
      const jobId = req.params.jobId;
      const jobRedis = await redisClient.get(`job:${jobId}`);
      
      if (!jobRedis) {
        return res.status(404).json({
          status: 404,
          mensagem: 'Job não encontrado ou expirado.'
        });
      }
  
      return res.status(200).json(JSON.parse(jobRedis));
  
    } catch (error) {
      console.error(`Erro ao buscar status do job ${req.params.jobId}:`, error.message);
      return res.status(500).json({
        status: 500, 
        mensagem: 'Erro interno no Gateway.'
      });
    }
  });

    // para o lado de GERENTE 
    // get gerente com quantidade de clientes por gerente
    app.get('/gerentes', async(req, res) => {
      try{
        const [gerenteResponse, contaResponse] = await Promise.all([
          fetch(`${process.env.MS_GERENTE_URL}/gerente`),
          fetch(`${process.env.MS_ACCOUNT_URL}/account`)
        ])
        if(!gerenteResponse.ok || !contaResponse.ok){
          return res.status(502).json({
            message: 'Erro ao comunicar com os ms´s'
          })
        }

        const gerentes = await gerenteResponse.json()
        const contas = await contaResponse.json()

        const resultado = gerentes.map(gerente =>{
          const quantClientes = contas.filter(
            conta => Number(conta.managerId) === Number(gerente.id)
          ).lenght;
          return{
            ...gerente,
            quantClientes
          }
        })

        resultado.sort((a,b) =>
          a.nome.localeCompare(b.nome, 'pt-BR')
        )
        return res.json(resultado)

      }catch (error){
        console.error('Erro ao compor dados de gerentes-> ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms gerente e ms-account'
        });
      }
    })

    //esqueci a merda do get por id
    app.get('/gerentes/:id', async(req, res) =>{
      try{
        const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.id}`);
        await encaminhaResponse(response, res);
      }catch(error){
        console.error('Erro ao buscar gerente x->  ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com o ms gerente'
        });
      }
    })

    app.get('/gerentes/:cpf', async(req, res) =>{
      try{
        const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.cpf}`);
        await encaminhaResponse(response, res);
      }catch(error){
        console.error('Erro ao buscar gerente x->  ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com o ms gerente'
        });
      }
    })

    // post gerente
    app.post('/gerentes', async(req, res) => {
      try{
        const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente`,
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(req.body)
          }
        );
        await encaminhaResponse(response, res);
      }catch(error){
        console.error('Erro ao criar gerentes->  ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms gerente'
        });
      }

    });

    //put gerente
    app.put('/gerentes/:id', async(req, res) => {
      try{
        const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.id}`,
          {
            method: 'PUT',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(req.body)
          }
        );
        await encaminhaResponse(response, res);
      }catch(error){
        console.error('Erro ao mudar gerentes->  ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms gerente'
        });
      }

    });

    //delete gerente
    app.delete('/gerentes/:id', async(req,res) =>{
      try{
        const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.id}`,
          {
            method: 'DELETE'
          }
        );
        await encaminhaResponse(response, res);
      } catch(error){
        console.error('Erro ao deletar gerente->  ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms gerente'
        })
      }
    });


    //pedaço CLIENTE 
    function formataSaldo(valor){
      return Number(valor).toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      } );
    }

    //lista cliente + saldo 
    app.get('/clientes', async(req, res) => {
      try{
        const clienteresponse = await fetch(`${process.env.MS_CLIENTE_URL}/clientes`);
        if (!clienteresponse.ok){
          return res.status(502).json({
            message: 'Erro ao comunicar com ms-cliente'
          });
        }
        const clientes = await clienteresponse.json();

        const resultado = await Promise.all(
          clientes.map(async (cliente) => {
            const contaresponse = await fetch(`${process.env.MS_ACCOUNT_URL}/account/findByCpf/${encodeURIComponent(cliente.cpf)}`);

            if (!contaresponse.ok){
              throw new Error(`Erro ao consultar o cliente  ${cliente.cpf}`);
            }

            const conta = await contaresponse.json();

            return {
              cpf: cliente.cpf,
              nome: cliente.nome,
              cidade: cliente.cidade,
              estado: cliente.estado,
              saldo: formataSaldo(conta.balanco)
            }

          })
        );

        return res.json(resultado);

      }catch (error){
        console.error('Erro ao catar os cliente-> ', error);
        res.status(502).json({
          message: 'Erro ao compor os dados'
        });
      }
    });


    //relatório
    app.get('/relatorio-clientes', async(req, res) => {
      try{
        //clientes
        const clienteresponse = await fetch(`${process.env.MS_CLIENTE_URL}/clientes`);
        if (!clienteresponse.ok){
          return res.status(502).json({
            message: 'Erro ao comunicar com ms-cliente'
          });
        }
        const clientes = await clienteresponse.json();

        //gerentes
        const gerenteresponse = await fetch(`${process.env.MS_GERENTE_URL}/gerente`);
        if (!gerenteresponse.ok){
          return res.status(502).json({
            message: 'Erro ao comunicar com ms-gerente'
          });
        }
        const gerentes = await gerenteresponse.json();

        const resultado = [];

        for (const cliente of clientes) {
          const contaresponse = await fetch(`${process.env.MS_ACCOUNT_URL}/account/findByCpf/${encodeURIComponent(cliente.cpf)}`);

          if (contaresponse.ok) {
            const conta = await contaresponse.json();

            const gerente = gerentes.find(g => String(g.cpf) === String(conta.managerId) || Number(g.id) === Number(conta.managerId));
          
            resultado.push ({
              cpfCliente: cliente.cpf,
              nomeCliente: cliente.nome,
              emailCliente: cliente.email,
              salario: formataSaldo(cliente.salario),
              numeroConta: conta.number,
              saldoConta: formataSaldo(conta.balanco),
              cpfGerente: gerente.cpf,
              nomeGerente: gerente.nome,
            });

          }
        }

        return res.json(resultado);

      }catch (error){
        console.error('Erro no relatório de clientes-> ', error);
        res.status(502).json({
          message: 'Erro ao compor os dados do relatório'
        });
      }
    });

    //tranferencia
    app.post('/account/transfer', async(req, res) =>{
      try {
        const response = await fetch(`${process.env.MS_ACCOUNT_URL}/account/transfer`,
          {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(req.body)
          }
        )
        await encaminhaResponse(response, res)
      } catch (error) {
        console.error('Erro ao realizar a transferencia -> ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms-account'
        })
      }
    });

    //operação 
    app.post('/account/operate', async(req, res) =>{
      try {
        const response = await fetch(`${process.env.MS_ACCOUNT_URL}/account/operate`,
          {
            method: 'POST',
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify(req.body)

          }
        )
        await encaminhaResponse(response,res)
      } catch (error) {
        console.error('Erro ao realizar a operação -> ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms-account'
        })
      }
    });

iniciarServidor();