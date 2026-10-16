--POPULAÇÃO DO BANCO DB-ACCOUNT @#$%¨&*())(*&¨%$%¨&*(*&¨%$))


--ARRANCA DADOS E COLOCA DE NOVO
TRUNCATE TABLE account, extrato, operation, transference; 

--ALTER TABLE account ADD COLUMN created_at integer;

--ACCOUNTS $%¨&*(*&¨%$&%)
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('001', 0.00, '11122233344', 999888777666, 20260921);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('002', 0.00, '22233344455', 999888777666, 20261008);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('003', 0.00, '33344455566', 999888777666, 20261004);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('004', 0.00, '44455566677', 999888777666, 20250304);

--EXTRATOS $%¨&*((*&¨$#$%¨&*()))
/*
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('001', 20260921, 700.00);

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('001', 20261008, 800.00);
*/
--OPERACOES $%¨*()(&¨%$#$¨(*&¨%#))

/*
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '001',
    '2026/09/21 18:31:00',
    'DEP',
    'operation',
    700.00,
    e.id
FROM extrato e
WHERE e.acc_number = '001'
  AND e.date_id = 20260921;

INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '001',
    '2026/10/08 18:31:00',
    'DEP',
    'operation',
    100.00,
    e.id
FROM extrato e
WHERE e.acc_number = '001'
  AND e.date_id = 20261008
*/