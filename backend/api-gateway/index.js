const express = require('express');
const app = express();
const port = 8080;

app.use(express.json());

app.get('/', (req, res) => {
  res.send('Olá Mundo!');
});

// Encaminha o cliente recebido do front para o ms-cliente
app.post('/api/solicitacoes', async (req, res) => {
  try {
    const response = await fetch(
      `${process.env.MS_CLIENTE_URL}/api/solicitacoes/registrar`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req.body)
      }
    );

    res.status(response.status).send();

  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: 'Erro ao comunicar com o ms-client'
    });
  }
})

app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});