const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
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

app.get("validacao", (req, res) => {
  const authHeader = req.header["authorization"];
  const token = authHeader && authHeader.split(" ")[1];

  if(!token) return res.status(403).json({error: "Não autorizado"});

  jwt.verify(token, "MONSTERBANK", (err, email) => {
    if(err) return res.status(403).json({error: "Não autorizado"});

    res.json({message: "AUTORIZADO", email});

  });

});



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

// para o CRUD de gerentes 
app.get('/gerentes', async (req, res) => {
  try {
    const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente`);
    await encaminhaResponse(response, res);
  } catch (error) {
    console.error('Erro a listar gerentes-> ', error);
    res.status(502).json({
      message: 'Erro ao comunicar com ms gerente'
    });
  }
});

//esqueci a merda do get por id
app.get('/gerentes/:id', async (req, res) => {
  try {
    const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.id}`);
    await encaminhaResponse(response, res);
  } catch (error) {
    console.error('Erro ao buscar gerente x->  ', error);
    res.status(502).json({
      message: 'Erro ao comunicar com o ms gerente'
    });
  }
})

app.post('/gerentes', async (req, res) => {
  try {
    const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.id}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(req.body)
      }
    );
    await encaminhaResponse(response, res);
  } catch (error) {
    console.error('Erro ao criar gerentes->  ', error);
    res.status(502).json({
      message: 'Erro ao comunicar com ms gerente'
    });
  }

});

app.put('/gerentes/:id', async (req, res) => {
  try {
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
  } catch (error) {
    console.error('Erro ao mudar gerentes->  ', error);
    res.status(502).json({
      message: 'Erro ao comunicar com ms gerente'
    });
  }

});

app.delete('/gerentes/:id', async (req, res) => {
  try {
    const response = await fetch(`${process.env.MS_GERENTE_URL}/gerente/${req.params.id}`,
      {
        method: 'DELETE'
      }
    );
    await encaminhaResponse(response, res);
  } catch (error) {
    console.error('Erro ao deletar gerente->  ', error);
    res.status(502).json({
      message: 'Erro ao comunicar com ms gerente'
    })
  }
});


app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});