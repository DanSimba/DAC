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
    return res.status(response.status).end();
  }
  try {
    return res.status(response.status).json(JSON.parse(texto));
  } catch {
    return res.status(response.status).send(texto);
  }
}

let redisClient;
let rabbitmqChannel;

async function iniciarServidor() {
  try {
    console.log("Conectando infra do API-Gateway");

    console.log("Conectando ao Redis...")
    redisClient = redis.createClient({ url : process.env.REDIS_URL });
    await redisClient.connect();
    console.log("Conectado ao Redis.")

    console.log("Conectando ao RabbitMQ...")
    const rabbitMqConnection = await amqp.connect(process.env.RABBITMQ_URL);
    rabbitmqChannel = await rabbitMqConnection.createChannel();
    console.log("Conectado ao RabbitMQ.");

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

app.post('/login', async (req, res) => {
  const { email, senha } = req.body;

  if (!email || !senha) {
    return res.status(400).json({
      status: 400,
      mensagem: 'E-mail e senha são obrigatórios.'
    });
  }

  try {
    const authResponse = await fetch(`${process.env.MS_AUTH_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, senha })
    });

    const data = await authResponse.json();

    if (!authResponse.ok) {
      return res.status(authResponse.status).json(data);
    }

    const usuarioInfo = data.usuario || data;

    const token = jwt.sign(
      {
        email: usuarioInfo.email,
        tipo: usuarioInfo.tipo
      },
      "MONSTERBANK",
      { expiresIn: "120m" }
    );

    return res.status(200).json({
      status: 200,
      mensagem: 'Autorizado.',
      token,
      dados: data
    });

  } catch (error) {
    console.error('Erro no login:', error.message);
    return res.status(500).json({
      status: 500,
      mensagem: 'Erro com o login.'
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
function validaJWT(req, res, next){
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')){
    return res.status(401).json({
      message: 'token não informado'
    })
  }
  const token = authHeader.split(' ')[1];
  try {
    const usuario = jwt.verify(
      token, 'MONSTERBANK'
    )
    req.usuario = usuario
    return next()
  } catch (error) {
    return res.status(401).json({
      message: 'token inválido ou expirado'
    })
    
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

// Rejeitar solicitações
// app.post('/solicitacoes/:cpf/rejeicao', async (req, res) => {
//   try {
//     const response = await fetch(`${process.env.MS_CLIENTE_URL}/solicitacoes/${req.params.cpf}/rejeicao`, {
//       method: 'POST',
//       headers: { 'Content-Type': 'application/json' },
//       body: JSON.stringify(req.body)});
//             return await encaminhaResponse(response, res);

//         } catch (error) {
//             console.error('Erro de rede ao acessar ms-cliente:', error.message);
//             return res.status(503).json({
//                 status: 503,
//                 mensagem: 'Serviço indisponível.'
//             });
//         }
//     });
// pq caralhos tinha 2 dessa eu não sei, mas mantem essa joça aqui 

    // Rejeitar solicitações
    app.post('/solicitacoes/:cpf/rejeicao', async (req, res) => {
      try {
        const response = await fetch(`${process.env.MS_CLIENTE_URL}/solicitacoes/${req.params.cpf}/rejeicao`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(req.body)
        });

        console.log("Entrei no Post para rejeitar uma solicitação.");

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
  app.post('/solicitacoes/:cpf/aprovacao', async (req, res) => {
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

    // Lista as solicitações
    app.get('/solicitacoes', async (req, res) => {
      try {
        const queryParams = new URLSearchParams(req.query).toString();
        const url = `${process.env.MS_CLIENTE_URL}/solicitacoes${queryParams ? '?' + queryParams : ''}`;
        const response = await fetch(url);

        console.log("Entrei no Get para listar todas as solicitações.");

    console.log("Entrei no Get para listar uma solicitação");

    return await encaminhaResponse(response, res);

  } catch (error) {
    console.error('Erro ao listar solicitação. ', error);
    return res.status(503).json({
      status: 503,
      mensagem: 'Serviço indisponível.'
    })
  }
});

    // para o lado de GERENTE 
    // get gerente
    app.get('/gerentes', async(req, res) => {
      try{
        const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente`);
        await encaminhaResponse(response, res);
      }catch (error){
        console.error('Erro a listar gerentes-> ', error);
        res.status(502).json({
          message: 'Erro ao comunicar com ms gerente'
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
            headers: {'Content-Type':'applicaation/json'},
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