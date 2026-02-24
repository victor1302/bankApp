# 🏦 bankApp

O **bankApp** é um sistema bancário **RESTful completo** desenvolvido em **Java (Spring Boot)**.  
Ele oferece autenticação segura com **JWT**, gestão de **usuários, contas bancárias, cartões de crédito, faturas e transações (PIX, crédito e débito)**, além de controle de acesso detalhado.

Este projeto foi construído com foco em **boas práticas**, **segurança**, **modularidade** e **escalabilidade**, pronto para produção e conteinerizado com **Docker**.

---

## 🚀 Funcionalidades

### 🔐 Autenticação e Autorização (JWT)
- Login seguro com geração de **JWT** (`POST /auth/login`).
- Registro de usuários (`POST /auth/register`).
- Proteção de rotas sensíveis com **roles** e **authorities** (`ROLE_ADMIN`, `ROLE_BASIC`).
- Tokens com **tempo de expiração configurável** via propriedades.

### 👥 Gestão de Usuários
- Cadastro de novos usuários com dados pessoais e tipo de usuário.
- Listagem paginada de usuários para administradores (`GET /api/v1/users`).
- Consulta paginada do usuário autenticado (`GET /api/v1/user`).
- Desativação de usuários por administradores (`PUT /api/v1/user/{id}`).
- Uso de **projeções JPA** para respostas leves.

### 🪙 Gerenciamento de Contas Bancárias
- Criação de conta para o usuário autenticado (`POST /api/v1/account`).
- Geração sequencial e única de número de conta.
- Associação direta entre usuário e conta.
- Desativação de conta pelo administrador (`PUT /api/v1/account/disable/{accountId}`).
- Controle de saldo e verificação de conta/usuário ativo.

### 💳 Cartões de Crédito e Faturas
- Emissão de **cartão de crédito vinculado à conta bancária** (`POST /api/v1/card`).
- Geração de número de cartão com **algoritmo de Luhn** para validação.
- Definição de **limite de crédito inicial** e controle de limite disponível.
- Geração e vinculação de faturas às compras realizadas com cartão.
- Pagamento total de faturas (`PUT /api/v1/transactions/pay/invoice/{id}`).

### 💰 Operações Financeiras e Transações (PIX, Crédito, Débito)
- **PIX**: transferência eletrônica instantânea entre contas, com débito imediato do saldo da conta de origem e crédito na conta de destino.
- **Crédito**: compras e lançamentos que utilizam o limite do cartão, gerando faturas e parcelas quando necessário.
- **Débito**: movimentações que debitam diretamente o saldo da conta, respeitando as regras de saldo mínimo.
- Regras de negócio para:
  - Validação de saldo disponível antes de concluir PIX e débitos.
  - Verificação de usuário e conta ativos.
  - Lançamento de exceção específica quando o saldo é insuficiente.
- Registro de todas as operações em um **ledger** (`LedgerEntry`) para rastreabilidade contábil.
- Controle de status da transação (`PENDING`, `COMPLETED`, etc.) e tipo de transação (`PIX`, `CREDIT`, `DEBIT`) via enums.

#### 📘 Ledger e Rastreamento Contábil (`LedgerEntry`)
O `LedgerEntry` funciona como o **livro-razão** do sistema:

- Cada operação relevante (PIX, crédito, débito, pagamento de fatura, geração de fatura, etc.) gera um ou mais registros de lançamento.
- Os lançamentos permitem **auditar** todo o histórico financeiro, mesmo que a estrutura de outras entidades (como `Transaction` ou `Invoice`) mude ao longo do tempo.

Principais informações mantidas em `LedgerEntry` (de forma conceitual):
- Valor da operação.
- Tipo de lançamento (`EntryType`, por exemplo: crédito ou débito).
- Status do lançamento (`EntryStatus`, por exemplo: pendente, confirmado).
- Referência para a origem do lançamento (`ReferenceType`), como transação, fatura ou parcela.
- Conta associada ao lançamento (conta debitada ou creditada).
- Datas de criação/efetivação do lançamento.

Exemplos de uso:
- **PIX entre duas contas**:
  - Um lançamento de **débito** (`DEBIT`) no ledger da conta de origem.
  - Um lançamento de **crédito** (`CREDIT`) no ledger da conta de destino.
- **Pagamento de fatura de cartão**:
  - Débito no ledger da conta corrente do usuário.
  - Crédito no ledger vinculado à fatura/cartão, permitindo rastrear exatamente como o limite foi restabelecido.

Esse desenho permite construir, no futuro, **extratos detalhados, relatórios financeiros e auditorias**, tudo baseado em `LedgerEntry` sem depender diretamente apenas das entidades de alto nível.

### 📄 Listagens Customizadas e Paginadas
- Suporte a **filtros e paginação** nas listagens de usuários e transações.
- **Projeções** (`UserProjection`, `TransactionProjection`) para respostas mais rápidas e leves.

### ⚠️ Tratamento Global de Exceções
- `GlobalExceptionHandler` para tratar exceções de regra de negócio como:
  - `AlreadyExistsException`
  - `BadCredentialException`
  - `AlreadyDisabledOrNotPresent`
  - `UserOrAccountDisabled`
- Respostas padronizadas com status HTTP adequados (por exemplo, `403 FORBIDDEN`).

---

## 🧩 Tecnologias Utilizadas

| Categoria | Tecnologia |
|------------|-------------|
| Linguagem | Java 17+ |
| Framework Principal | Spring Boot 3 |
| ORM / Persistência | Spring Data JPA (Hibernate) |
| Segurança | Spring Security + JWT |
| Banco de Dados | PostgreSQL |
| Contêineres | Docker, Docker Compose |
| Build | Maven |
| Documentação | Swagger / OpenAPI (se configurado) |
| Utilitários | Lombok, Java Faker |

---

## 🏗️ Estrutura do Projeto

```
bankApp/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/bankapp/
 │   │   │   ├── controller/        # Controllers REST (UserController, AccountController, CardController, TreansactionController, InstallmentController)
 │   │   │   ├── dto/               # DTOs de entrada e saída (User, Account, Card, Transaction, Invoice, LedgerEntry, Installments)
 │   │   │   ├── entity/            # Entidades JPA (User, Account, Card, Transaction, Invoice, Installment, LedgerEntry, Role, enums)
 │   │   │   ├── exception/         # Exceções de regra de negócio + GlobalExceptionHandler
 │   │   │   ├── interfaces/        # Projeções JPA (UserProjection, TransactionProjection, AccountProjection)
 │   │   │   ├── repository/        # Repositórios Spring Data JPA (UserRepository, AccountRepository, TransactionRepository, ...)
 │   │   │   ├── security/          # Configurações de segurança, filtros e TokenService (JWT)
 │   │   │   ├── service/           # Serviços de domínio (UserService, AccountService, CardService, TransactionService, InvoiceService, LedgerService, InstallmentService)
 │   │   │   └── data/              # Utilitários e inicialização de dados (LuhnAlgorithm, DataInitializer)
 │   │   └── resources/
 │   │       ├── application.yml    # Configurações do ambiente (DB, security, JWT, etc.)
 │   │       └── schema.sql         # (opcional) Script de criação de tabelas
 │   └── test/
 │       └── java/com/bankapp/     # Testes unitários e de integração
 ├── docker-compose.yml             # Subida de banco de dados e/ou aplicação via contêiner
 ├── pom.xml                        # Configuração do Maven e dependências
 └── README.md
```

---

## ⚙️ Requisitos

- **Java 17** ou superior
- **Maven 3.8+**
- **Docker** e **Docker Compose** (para infraestrutura com contêineres)
- Banco de dados **PostgreSQL** (pode ser via Docker)

---

## ▶️ Como Executar o Projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/victor1302/bankApp.git
cd bankApp
```

### 2. Executar infraestrutura com Docker (opcional, recomendado)

Se o `docker-compose.yml` estiver configurado com PostgreSQL (e opcionalmente a aplicação), você pode subir o ambiente com:

```bash
docker-compose up -d
```

Isso irá levantar os serviços necessários (por exemplo, banco de dados PostgreSQL) em contêineres Docker.

### 3. Configurar o banco de dados (quando necessário)

Caso use um banco local ou queira customizar, atualize o arquivo `src/main/resources/application.yml` com as credenciais do seu PostgreSQL, por exemplo:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankapp
    username: seu_usuario
    password: sua_senha
```

### 4. Executar a aplicação com Maven

```bash
./mvnw spring-boot:run
```

Ou, se tiver Maven instalado globalmente:

```bash
mvn spring-boot:run
```

A aplicação iniciará na porta configurada em `application.yml` (por padrão, `8080`, se não alterado).

---

## 🧪 Testes

O projeto utiliza:
- **JUnit 5 (Jupiter)**
- **Mockito**
- **Spring Test** para suporte a componentes Spring quando necessário

Para executar os testes:

```bash
./mvnw test
```

ou

```bash
mvn test
```

Os testes estão localizados em `src/test/java/com/bankapp/` e cobrem principalmente regras de negócio em serviços e operações de transação/fatura.

---

## 💡 Futuras Implementações

- Integração com Pix e boletos com conciliação automática.
- Relatórios financeiros com exportação (PDF/CSV).
- Notificações por e-mail.
- Painel administrativo (front-end com foco em Angular).
- Ampliação da cobertura de testes automatizados (unitários e integração).

---

> ⚠️ **Projeto ainda em andamento!**  
> Algumas funcionalidades, como transferências e histórico de transações detalhado, ainda estão sendo finalizadas e podem sofrer alterações nas próximas versões.

---

## 👨‍💻 Autor

**Victor Oliveira**  
Desenvolvedor backend em formação, com foco em **Java, Spring Boot e arquitetura REST**.  
💼 [LinkedIn](https://www.linkedin.com/in/victor-oliveira-324013226/) | 💻 [GitHub](https://github.com/victor1302)
