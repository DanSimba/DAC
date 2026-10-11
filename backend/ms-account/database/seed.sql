--POPULAÇÃO DO BANCO DB-ACCOUNT @#$%¨&*())(*&¨%$%¨&*(*&¨%$))


--ARRANCA DADOS E COLOCA DE NOVO
TRUNCATE TABLE account, extrato, operation, transference; 

--ACCOUNTS $%¨&*(*&¨%$&%)
INSERT INTO account (number, balanco, client_cpf, manager_id)
VALUES ('001', 800.00, '11122233344', 999888777666);

INSERT INTO account (number, balanco, client_cpf, manager_id)
VALUES ('002', 10856.80, '22233344455', 999888777666);

INSERT INTO account (number, balanco, client_cpf, manager_id)
VALUES ('003', 500.40, '33344455566', 999888777666);

--EXTRATOS $%¨&*((*&¨$#$%¨&*()))
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('001', 20260921, 700.00);

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('001', 20261008, 800.00);

--OPERACOES $%¨*()(&¨%$#$¨(*&¨%#))
--qndo colocar uma oper/transf, ela já vai vir completa do back,
--sem a necessidade de fazer este select para encontrar o extrato no bd,
--estou fazendo isso só para colocar o dado pla primeira vez
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