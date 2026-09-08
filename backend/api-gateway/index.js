const express = require('express');
const cors = require('cors');
const app = express();
const port = 8080;

app.use(cors({
  origin: 'http://localhost:4200',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(express.json());

app.get('/', (req, res) => {
  res.send('Olá Mundo!');
});

// Encaminha o cliente recebido do front para o ms-cliente
app.post('/clientes', async (req, res) => {
  console.log("Cheguei no Gateway.");
  try {
    const response = await fetch(
      `${process.env.MS_CLIENTE_URL}/clientes/solicitacao/registrar`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body)
      }
    );

    const responseBodyText = await response.text();

    let responseBodyJson = null;
    try {
      responseBodyJson = JSON.parse(responseBodyText);
    } catch (e) {
      // Se falhar o parse, significa que não é um JSON válido (mantém como texto)
    }

    res.status(response.status);

    if (responseBodyJson) {
      console.log(responseBodyJson);
      res.json(responseBodyJson);
    } else {
      console.log(responseBodyText);
      res.send(responseBodyText);
    }
  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: 'Erro ao comunicar com o ms-client.'
    });
  }
})

app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});