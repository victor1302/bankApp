# 🏦 bankApp

O **bankApp** é um sistema bancário **RESTful completo** desenvolvido em **Java (Spring Boot)**.  
Ele oferece autenticação segura com **JWT**, gestão de **usuários e contas bancárias**, operações financeiras básicas e controle de acesso detalhado.

Este projeto foi construído com foco em **boas práticas**, **segurança**, **modularidade** e **escalabilidade**, pronto para produção.

---

## 🚀 Funcionalidades

### 🔐 Autenticação e Autorização (JWT)
- Login e logout seguros.
- Renovação automática do token.
- Proteção de rotas sensíveis com **roles** e **authorities**.
- Tokens com **tempo de expiração configurável**.

### 👥 Gestão de Usuários
- Cadastro e atualização de perfis.
- Ativação e desativação de contas.
- Paginação e ordenação configuráveis.
- DTOs otimizados para respostas leves.

### 💳 Gerenciamento de Contas Bancárias
- Criação de conta quando o usuário desejar.
- Geração **sequencial e única** de número de conta.
- Visualização dos dados da conta do usuário autenticado.
- Associação direta entre usuário e conta.

### 💰 Operações Financeiras
- Consulta de **saldo**.
- Registro de **transferências** entre contas *(em produção)*.
- Histórico de transações *(em produção)*.
- Regras de negócio com **validações automáticas** (ex: saldo insuficiente) *(em produção)*.

### 📄 Listagens Customizadas e Paginadas
- Suporte a **filtros dinâmicos** via *query params*.
- **Ordenação por múltiplos campos**.
- **Paginação configurável** com respostas em DTOs otimizados.
- Projeções para respostas mais rápidas.

---

## 🧩 Tecnologias Utilizadas

| Categoria | Tecnologia |
|------------|-------------|
| Linguagem | Java 17+ |
| Framework Principal | Spring Boot 3 |
| ORM / Persistência | Spring Data JPA (Hibernate) |
| Segurança | Spring Security + JWT |
| Banco de Dados | PostgreSQL |
| Contêineres | Docker / Docker Compose |
| Build | Maven |
| Documentação | Swagger / OpenAPI |
| Utilitários | Lombok |

---

## 🏗️ Estrutura do Projeto

```
bankApp/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/bankapp/
 │   │   │   ├── controller/        # Endpoints REST
 │   │   │   ├── dto/               # Objetos de transferência de dados
 │   │   │   ├── entity/            # Entidades JPA
 │   │   │   ├── exception/         # Tratamento de erros e exceções customizadas
 │   │   │   ├── repository/        # Interfaces JPA
 │   │   │   ├── security/          # Configurações JWT e filtros
 │   │   │   ├── service/           # Lógica de negócios
 │   │   └── resources/
 │   │       ├── application.yml    # Configurações do ambiente
 │   │       └── schema.sql         # (opcional) Script de criação de tabelas
 │   └── test/                      # Testes unitários e de integração
 ├── pom.xml
 └── README.md
```

---

## 💡 Futuras Implementações

- Integração com Pix e boletos.
- Relatórios financeiros com exportação (PDF/CSV).
- Notificações por e-mail.
- Painel administrativo (front-end com foco em Angular).

---

> 
> ⚠️ **Projeto ainda em andamento!**  
> Algumas funcionalidades, como transferências e histórico de transações, ainda estão sendo finalizadas e podem sofrer alterações nas próximas versões.

---

## 👨‍💻 Autor

**Victor Oliveira**  
Desenvolvedor backend em formação, com foco em **Java, Spring Boot e arquitetura REST**.  
💼 [LinkedIn](https://www.linkedin.com/in/victor-oliveira-324013226/) | 💻 [GitHub](https://github.com/victor1302)
