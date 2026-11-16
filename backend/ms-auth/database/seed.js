db.usuarios.deleteMany({});

db.usuarios.insertMany([
  {
    login: 'cli1@bantads.com.br',
    cpf: '12912861012',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$sIVuXQyZC5K5JG1xY+o9Dg$ctd2ylyWVhXQn0wmy1TGOUIOfd8ItQ8au1pi7Ru8bys',
    tipo: 'CLIENTE',
    ativo: true
  },
  {
    login: 'cli2@bantads.com.br',
    cpf: '09506382000',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$N6P6EQZFrAGVksK2vHHJcA$aZAOfZmxzDCR4CnASH8Eo569Zz9gVGl7Dy6UvIeJmm0',
    tipo: 'CLIENTE',
    ativo: true
  },
  {
    login: 'cli3@bantads.com.br',
    cpf: '85733854057',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$IrB2JrkQXtUVN3yDMBwX2Q$HkoeNVKp7grwzFaXKTYRAtmoZcQvUCYEXtd4yECzmtw',
    tipo: 'CLIENTE',
    ativo: true
  },
  {
    login: 'cli4@bantads.com.br',
    cpf: '58872160006',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$YldbtdRNMOqcDs+0Z7iQzw$Hwb+xpDkCmOgvXgCAxxibPGnj9FoxCJH1isQ1Vtvr4Q',
    tipo: 'CLIENTE',
    ativo: true
  },
  {
    login: 'cli5@bantads.com.br',
    cpf: '76179646090',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$w1eYHi+mPoB9aMVZ8vZ5IA$VwQ0faYAQ0iLF5OSiKK1JCPQeuw0vZv1UWa7KV9j4kM',
    tipo: 'CLIENTE',
    ativo: true
  },
  {
    login: 'ger1@bantads.com.br',
    cpf: '98574307084',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$z13v6iKaugh6Kf0GxiNnCQ$U1rlgD6RrHDTb8a20QC8CvawHygiPSv72AGiqiS9kyE',
    tipo: 'GERENTE',
    ativo: true
  },
  {
    login: 'ger2@bantads.com.br',
    cpf: '64065268052',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$Ds4sOFTmLcR6C5UVXwVTNQ$I7AqXlyIgafhhlyuaR/kWA8441e/YTrNxd+MYMkQbOA',
    tipo: 'GERENTE',
    ativo: true
  },
  {
    login: 'ger3@bantads.com.br',
    cpf: '23862179060',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$M529SZs77vQunr+bSH8/TQ$vuHnDrr9zJhLD9hmJPQ15fTPHCiuBGJpihCLwynvmdI',
    tipo: 'GERENTE',
    ativo: true
  },
  {
    login: 'ger4@bantads.com.br',
    cpf: '40501740066',
    senha: '$argon2id$v=19$m=16384,t=2,p=1$Roc7gnBU1dHUEweZ22CviQ$jeC0hqkGKs2CI6eqa4PvDZY98/7YDaRw+vGMZgvAstk',
    tipo: 'GERENTE',
    ativo: true
  }
]);
