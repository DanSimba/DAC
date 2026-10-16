DROP TABLE IF EXISTS gerente CASCADE;

CREATE TABLE gerente (
    id                  BIGSERIAL       PRIMARY KEY,
    nome                VARCHAR(100)    NOT NULL,
    cpf                 CHAR(11)        NOT NULL UNIQUE,
    email               VARCHAR(150)    NOT NULL UNIQUE,
    telefone            VARCHAR(20)     NOT NULL UNIQUE,
    ativo               BOOLEAN         NOT NULL DEFAULT TRUE
);