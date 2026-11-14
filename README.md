# 🦇 BANTADS — Monster High Edition 💀
### *Where every monster's money has a little mystery.*

<p align="center">
  <img src="https://img.shields.io/badge/Monster%20High-Edition-ff69b4?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Status-In%20Development-800080?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-Microservices-1a1a1a?style=for-the-badge" />
</p>

<p align="center">
  <i>Welcome to the most monstrously fabulous bank in the world!</i>
</p>

---

## 🕸️ Sobre o Projeto

Bem-vindos ao **BANTADS**, o banco onde até os monstros precisam cuidar da vida financeira!

Inspirado no universo de **Monster High**, este projeto é uma aplicação de Internet Banking desenvolvida como trabalho acadêmico para a disciplina de **Desenvolvimento de Aplicações Corporativas (DS152)**, do curso de Tecnologia em Análise e Desenvolvimento de Sistemas da Universidade Federal do Paraná (UFPR).

Com uma identidade visual inspirada no estilo gótico, fashion e sobrenatural de Monster High, o BANTADS combina uma interface temática com funcionalidades bancárias reais, proporcionando uma experiência monstruosamente completa.

Aqui, vampiros, lobisomens, múmias e outros seres sobrenaturais também precisam de uma conta bancária!

## 💖 Funcionalidades

### 🦇 Área do Cliente
- **Autocadastro:** solicitação de abertura de conta bancária.
- **Login e Logout:** autenticação segura dos usuários.
- **Dashboard:** visualização das informações da conta e saldo disponível.
- **Depósitos:** adição de valores à conta.
- **Saques:** retirada de valores respeitando o saldo disponível.
- **Transferências:** movimentação de dinheiro entre contas.
- **Extrato bancário:** consulta ao histórico de movimentações por período.

### 🕷️ Área do Gerente
- **Dashboard administrativo:** acompanhamento das solicitações de cadastro.
- **Aprovação de clientes:** criação de contas após análise das solicitações.
- **Rejeição de clientes:** recusa de solicitações com justificativa.
- **Consulta de clientes:** visualização e pesquisa dos clientes cadastrados.
- **Gerenciamento de gerentes:** cadastro, atualização, listagem e remoção.
- **Relatórios:** visualização consolidada das informações dos clientes.

## 🧛 Arquitetura e Tecnologias

O sistema utiliza uma arquitetura distribuída baseada em **Microsserviços**, permitindo a separação das responsabilidades e a comunicação entre diferentes serviços.

### Arquitetura

- Microservices Architecture
- API Gateway
- Database per Service
- CQRS (Command Query Responsibility Segregation)
- Event Sourcing
- SAGA Orchestration
- API Composition
- Comunicação REST e mensageria assíncrona

### Tecnologias

| Categoria | Tecnologias |
|---|---|
| Front-end | Angular / React / Vue.js |
| API Gateway | Node.js |
| Back-end | Java, Spring Boot |
| Banco de dados | PostgreSQL, MongoDB |
| Cache e sessões | Redis |
| Mensageria | RabbitMQ |
| Infraestrutura | Docker |
| Autenticação | JWT e Argon2 |

## ⚰️ Microsserviços

O BANTADS é dividido em serviços independentes, cada um responsável por um domínio específico:

| Serviço | Responsabilidade |
|---|---|
| MS Cliente | Cadastro e gerenciamento de clientes |
| MS Conta | Contas, saldos e movimentações financeiras |
| MS Gerente | Gerenciamento dos gerentes bancários |
| MS Auth | Autenticação e gerenciamento de usuários |
| API Gateway | Ponto central de entrada das requisições |
| Orquestrador SAGA | Coordenação de transações distribuídas |
| MS Email | Envio de notificações por e-mail |

## 🖤 Identidade Visual

Inspirado no universo de Monster High, o projeto utiliza uma identidade visual baseada em elementos góticos e fashion.

**Paleta temática:**
- 🖤 Preto — elegância e mistério.
- 💗 Rosa pink — personalidade e atitude.
- 💜 Roxo — magia e sobrenatural.
- 🤍 Branco — contraste e legibilidade.

A proposta é unir a atmosfera monstruosa de Monster High à experiência moderna de um Internet Banking, criando uma interface que foge completamente dos padrões tradicionais de sistemas bancários.

## 🧪 Como executar o projeto

Clone o repositório:

```bash
git clone URL_DO_REPOSITORIO
```

Entre na pasta do projeto:

```bash
cd NOME_DO_PROJETO
```

Configure as variáveis de ambiente conforme o arquivo `.env.example`.

Inicie os serviços utilizando Docker Compose:

```bash
docker compose up --build
```

> Consulte as instruções específicas de cada serviço para configurar e executar o front-end e os componentes necessários.

## 👻 Projeto Acadêmico

**Universidade Federal do Paraná — UFPR**

- Curso: Tecnologia em Análise e Desenvolvimento de Sistemas (TADS)
- Disciplina: DS152 — Desenvolvimento de Aplicações Corporativas
- Projeto: BANTADS — Internet Banking do TADS
- Período: 2026/2

---

<p align="center">
  🦇 <b>BE YOURSELF. BE UNIQUE. BE MONSTROUS.</b> 🦇
  <br/>
  <i>At BANTADS, even monsters deserve financial freedom.</i>
</p>

<p align="center">
  <sub>Made with 💗, a little magic and a lot of monster attitude.</sub>
</p>