--POPULAÇÃO DO BANCO DB-ACCOUNT-READ @#$%¨&*())(*&¨%$%¨&*(*&¨%$))

--ARRANCA DADOS E COLOCA DE NOVO
TRUNCATE TABLE account, eventos; 

--ACCOUNTS $%¨&*(*&¨%$&%)

--CONTAS REQS
--catharyna
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('1291', 800.00, '12912861012', 98574307084, 20000101);

--cleudonio
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('0950', 10000.00, '09506382000', 64065268052, 19901010);

--catianna
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('8573', 3700.00, '85733854057', 23862179060, 20121212);

--cutardo
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('5887', 150000.00, '58872160006', 98574307084, 20220222);

--coandrya
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('7617', 1500.00, '76179646090', 64065268052, 20260101);

--EVENTOS $%¨&*(*&¨%$&%)

--CATHARYNA (1291)
INSERT INTO eventos (objeto_id, tipo, payload, versao, datetime) VALUES
('1291', 'CRIADO',   '{"saldo": 0.00, "cpf_cliente": "12912861012", "cpfGerente": "98574307084"}', 1, '2000-01-01 00:00:00'),
('1291', 'DEPOSITO', '{"valor": 1000.00, "saldo": 1000.00}', 2, '2020-01-01 10:00:00'),
('1291', 'DEPOSITO', '{"valor": 900.00, "saldo": 1900.00}',  3, '2020-01-01 11:00:00'),
('1291', 'SAQUE',    '{"valor": 550.00, "saldo": 1350.00}',  4, '2020-01-01 12:00:00'),
('1291', 'SAQUE',    '{"valor": 350.00, "saldo": 1000.00}',  5, '2020-01-01 13:00:00'),
('1291', 'DEPOSITO', '{"valor": 2000.00, "saldo": 3000.00}', 6, '2020-01-10 15:00:00'),
('1291', 'SAQUE',    '{"valor": 500.00, "saldo": 2500.00}',  7, '2020-01-15 08:00:00'),
('1291', 'TRANSFERENCIA_ORIGEM', '{"valor": 1700.00, "saldo": 800.00, "conta_origem": "1291", "conta_destino": "0950"}', 8, '2020-01-20 12:00:00');

--CLEUDONIO (0950)
INSERT INTO eventos (objeto_id, tipo, payload, versao, datetime) VALUES
('0950', 'CRIADO',   '{"saldo": 0.00, "cpf_cliente": "09506382000", "cpfGerente": "64065268052"}', 1, '1990-10-10 00:00:00'),
('0950', 'TRANSFERENCIA_DESTINO', '{"valor": 1700.00, "saldo": 1700.00, "conta_origem": "1291", "conta_destino": "0950"}', 2, '2020-01-20 12:00:00'),
('0950', 'DEPOSITO', '{"valor": 1000.00, "saldo": 2700.00}',  3, '2026-01-01 12:00:00'),
('0950', 'DEPOSITO', '{"valor": 5000.00, "saldo": 7700.00}',  4, '2026-01-02 10:00:00'),
('0950', 'SAQUE',    '{"valor": 200.00, "saldo": 7500.00}',   5, '2026-01-10 10:00:00'),
('0950', 'DEPOSITO', '{"valor": 7000.00, "saldo": 14500.00}', 6, '2026-02-05 10:00:00'),
('0950', 'SAQUE',    '{"valor": 4500.00, "saldo": 10000.00}', 7, '2026-03-06 11:00:00');

--CATIANNA (8573)
INSERT INTO eventos (objeto_id, tipo, payload, versao, datetime) VALUES
('8573', 'CRIADO',   '{"saldo": 0.00, "cpf_cliente": "85733854057", "cpfGerente": "23862179060"}', 1, '2012-12-12 00:00:00'),
('8573', 'DEPOSITO', '{"valor": 4500.00, "saldo": 4500.00}', 2, '2026-05-05 10:00:00'),
('8573', 'SAQUE',    '{"valor": 800.00, "saldo": 3700.00}',  3, '2026-05-06 10:00:00');

--CUTARDO (5887)
INSERT INTO eventos (objeto_id, tipo, payload, versao, datetime) VALUES
('5887', 'CRIADO',   '{"saldo": 0.00, "cpf_cliente": "58872160006", "cpfGerente": "98574307084"}', 1, '2022-02-22 00:00:00'),
('5887', 'DEPOSITO', '{"valor": 150000.00, "saldo": 150000.00}', 2, '2026-06-01 10:00:00');

--COANDRYA (7617)
INSERT INTO eventos (objeto_id, tipo, payload, versao, datetime) VALUES
('7617', 'CRIADO',   '{"saldo": 0.00, "cpf_cliente": "76179646090", "cpfGerente": "64065268052"}', 1, '2026-01-01 00:00:00'),
('7617', 'DEPOSITO', '{"valor": 1500.00, "saldo": 1500.00}', 2, '2026-07-01 10:00:00');

