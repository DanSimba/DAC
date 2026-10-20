const express = require('express');
const cors = require('cors');
const crypto = require('crypto');
const { timeStamp } = require('console');
const redis = require('redis');
const amqp = require('amqplib');

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
  if(!texto){
    return res.status(response.status).end();
  }
  try{
    return res.status(response.status).json(JSON.parse(texto));
  }catch{
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
    console.log("Conectado ao RabbitMQ.")

    app.get('/', (req, res) => {
      res.send('Olá Mundo!');
    });

    //pedaço SOLICITAÇÂO 
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
          jobId : sagaId,
          status : "PENDENTE",
          resultType : null,
          dominio : "clientes",
          resourceId : cpf,
          erro : null
        };

        await redisClient.set(
          `job:${sagaId}`,
          JSON.stringify(jobEstado),
          {EX : 300}
        );

        const sagaMessage = {
          sagaId : sagaId,
          tipo : "aprovar-cliente",
          timestamp : new Date().toISOString(),
          payload : { cpf : cpf }
        };

        rabbitmqChannel.sendToQueue(
          'saga.cmd',
          Buffer.from(JSON.stringify(sagaMessage))
        );

        return res.status(202).json({ jobId : sagaId});

      } catch (error) {
        console.error(`Erro de rede ao rejeitar CPF ${req.params.cpf}:`, error.message);
        return res.status(503).json({
          status: 503, 
          mensagem: 'Serviço indisponível.' 
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

        return await encaminhaResponse(response, res);

      } catch (error) {
        console.error('Erro ao listar solicitações. ', error);
        return res.status(503).json({
            status: 503,
            mensagem: 'Serviço indisponível.'
          })
      }
    });

    // Lista uma solicitação
    app.get('/solicitacoes/:cpf', async (req, res) => {
      try {
        const response = await fetch(`${process.env.MS_CLIENTE_URL}/solicitacoes/${req.params.cpf}`);

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
    });

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

    app.listen(port, () => {
      console.log(`Servidor rodando em http://localhost:${port}`);
    });
  } catch(error) {
    console.error("Falha ao iniciar os serviços do API-Gateway: ", error);
    process.exit(1);
  }
}

iniciarServidor();