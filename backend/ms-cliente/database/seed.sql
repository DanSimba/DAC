--usando as mesmas merdas de dados da porra do documento do razer
--pra não ter problema com a porra de dado sujo nessa bosta
TRUNCATE TABLE clientes;

INSERT INTO clientes
    (cpf, nome, email, telefone, salario, logradouro, numero,
     complemento, cep, cidade, uf)
VALUES
    ('12912861012', 'Catharyna', 'cli1@bantads.com.br',
     '41999991001', 10000.0000, 'Rua das Flores', 101,
     NULL, '80010000', 'Curitiba', 'PR'),

    ('09506382000', 'Cleuddônio', 'cli2@bantads.com.br',
     '41999991002', 20000.0000, 'Avenida Brasil', 202,
     'Apto 2', '80020000', 'Curitiba', 'PR'),

    ('85733854057', 'Catianna', 'cli3@bantads.com.br',
     '41999991003', 3000.0000, 'Rua das Araucárias', 303,
     NULL, '80030000', 'Curitiba', 'PR'),

    ('58872160006', 'Cutardo', 'cli4@bantads.com.br',
     '41999991004', 500.0000, 'Rua XV de Novembro', 404,
     NULL, '80040000', 'Curitiba', 'PR'),

    ('76179646090', 'Coândrya', 'cli5@bantads.com.br',
     '41999991005', 1500.0000, 'Rua Paraná', 505,
     'Casa', '80050000', 'Curitiba', 'PR');