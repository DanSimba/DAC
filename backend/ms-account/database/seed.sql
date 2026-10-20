--POPULAÇÃO DO BANCO DB-ACCOUNT @#$%¨&*())(*&¨%$%¨&*(*&¨%$))


--ARRANCA DADOS E COLOCA DE NOVO
TRUNCATE TABLE account, extrato, operation, transference; 

--ALTER TABLE account ADD COLUMN created_at integer;

--ACCOUNTS $%¨&*(*&¨%$&%)

--JURO Q É SÓ PRA TESTAR O COMPOSITION

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('1291', 800.00, '12912861012', 999888777666, 20000101);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('0950', 10000.00, '09506382000', 999888777666, 19901010);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('8573', 200.00, '85733854057', 999888777666, 20121212);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('5887', 150000.00, '58872160006', 999888777666, 20220222);

INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('7617', 1500.00, '76179646090', 999888777666, 20250101);

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