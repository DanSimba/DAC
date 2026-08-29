const express = require('express');
const app = express();

app.use(express.json());

app.get('/', (req, res) => {
  res.send('Olá Mundo!');
});

app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});