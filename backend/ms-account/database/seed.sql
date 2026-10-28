--POPULAÇÃO DO BANCO DB-ACCOUNT @#$%¨&*())(*&¨%$%¨&*(*&¨%$))


--ARRANCA DADOS E COLOCA DE NOVO
TRUNCATE TABLE account, extrato, operation, transference; 

--ALTER TABLE account DROP COLUMN name_origin;
--ALTER TABLE transference ADD name_origin VARCHAR(255);

--ACCOUNTS $%¨&*(*&¨%$&%)

--CONTAS REQS
--catharyna
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('1291', 800.00, '98574307084', 12912861012, 20000101);

--cleudonio
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('0950', 10000.00, '64065268052', 09506382000, 19901010);

--catianna
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('8573', 3700.00, '23862179060', 85733854057, 20121212);

--cutardo
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('5887', 150000.00, '98574307084', 58872160006, 20220222);

--coandrya
INSERT INTO account (number, balanco, client_cpf, manager_id, created_at)
VALUES ('7617', 1500.00, '64065268052', 76179646090, 20260101);

--EXTRATOS $%¨&*((*&¨$#$%¨&*()))
--CUTARDO
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('5887', 20260601, 150000.00);

INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '5887',
    '01/06/2026 10:00',
    'DEP',
    'operation',
    150000.00,
    e.id
FROM extrato e
WHERE e.acc_number = '5887'
  AND e.date_id = 20260601;


--CATHARYNA
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('1291', 20200101, 1000.00);

    INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
    SELECT
        '1291',
        '01/01/2020 10:00',
        'DEP',
        'operation',
        1000.00,
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20200101;

    INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
    SELECT
        '1291',
        '01/01/2020 11:00',
        'DEP',
        'operation',
        900.00,
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20200101;

    INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
    SELECT
        '1291',
        '01/01/2020 12:00',
        'SAC',
        'operation',
        550.00,
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20200101;

    INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
    SELECT
        '1291',
        '01/01/2020 13:00',
        'SAC',
        'operation',
        350.00,
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20000101;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('1291', 20200110, 3000.00);

    INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
    SELECT
        '1291',
        '10/01/2020 15:00',
        'DEP',
        'operation',
        2000.00,
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20200110;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('1291', 20200115, 2500.00);

    INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
    SELECT
        '1291',
        '01/01/2020 08:00',
        'SAC',
        'operation',
        500.00,
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20200115;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('1291', 20200120, 800.00);

    INSERT INTO transference (type, cpf_origin, name_origin, acc_origin, acc_destiny, value, datetime, extrato_id)
    SELECT
        'transference',
        '12912861012',
        'Catharyna',
        '1291',
        '0950',
        1700.00,
        '20/01/2020 12:00',
        e.id
    FROM extrato e
    WHERE e.acc_number = '1291'
      AND e.date_id = 20200120;


--CLEUDONIO
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('0950', 20200120, 1700.00);
    INSERT INTO transference (type, cpf_origin, name_origin, acc_origin, acc_destiny, value, datetime, extrato_id)
    SELECT
        'transference',
        '12912861012',
        'Catharyna',
        '1291',
        '0950',
        1700.00,
        '20/01/2020 12:00',
        e.id
    FROM extrato e
    WHERE e.acc_number = '0950'
      AND e.date_id = 20200120;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('0950', 20260101, 2700.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '0950',
    '01/01/2026 12:00',
    'DEP',
    'operation',
    1000.00,
    e.id
FROM extrato e
WHERE e.acc_number = '0950'
  AND e.date_id = 20260101;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('0950', 20260102, 7700.00);

INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '0950',
    '02/01/2026 10:00',
    'DEP',
    'operation',
    5000.00,
    e.id
FROM extrato e
WHERE e.acc_number = '0950'
  AND e.date_id = 20260102;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('0950', 20260110, 7500.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '0950',
    '10/01/2026 10:00',
    'SAC',
    'operation',
    200.00,
    e.id
FROM extrato e
WHERE e.acc_number = '0950'
  AND e.date_id = 20260110;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('0950', 20260205, 14500.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '0950',
    '05/02/2026 10:00',
    'DEP',
    'operation',
    7000.00,
    e.id
FROM extrato e
WHERE e.acc_number = '0950'
  AND e.date_id = 20260205;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('0950', 20260306, 10000.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '0950',
    '06/03/2026 11:00',
    'SAC',
    'operation',
    4500.00,
    e.id
FROM extrato e
WHERE e.acc_number = '0950'
  AND e.date_id = 20260306;

--CATIANNA
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('8573', 20260505, 4500.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '8573',
    '05/05/2026 10:00',
    'DEP',
    'operation',
    4500.00,
    e.id
FROM extrato e
WHERE e.acc_number = '8573'
  AND e.date_id = 20260505;

INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('8573', 20260506, 3700.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '8573',
    '06/05/2026 10:00',
    'SAC',
    'operation',
    800.00,
    e.id
FROM extrato e
WHERE e.acc_number = '8573'
  AND e.date_id = 20260506;

--COANDRYA
INSERT INTO extrato (acc_number, date_id, saldo_apos)
VALUES ('7617', 20260701, 1500.00);
INSERT INTO operation (acc_number, datetime, side, type, value, extrato_id)
SELECT --ja encontra extrato respectivo
    '7617',
    '01/07/2026 10:00',
    'DEP',
    'operation',
    1500.00,
    e.id
FROM extrato e
WHERE e.acc_number = '7617'
  AND e.date_id = 20260701;