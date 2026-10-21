# Mercado - Cadastro de Produtos

Atividade de Testes de Integração (FATESG 2026/02).

- **Back-end:** Spring Boot 4 + JPA/Hibernate, API REST em `/api/produtos`
- **Front-end:** Angular (pasta `frontend`), tela com cadastro, edição e exclusão
- **Banco:** SQL Server, banco `Mercado` (a tabela `PRODUTO` é criada sozinha pelo Hibernate)
- **Testes:** Selenium WebDriver (insert, update e delete pela tela, conferindo no banco) e testes de unidade do serviço com Mockito

## Pré-requisitos

- Java 21, Node 24 e Google Chrome
- SQL Server rodando na porta 1433, com o login configurado em `src/main/resources/credenciais.properties` (copie de `credenciais.example.properties`) e o banco criado:

```sql
CREATE DATABASE Mercado;
```

## Como rodar

```bash
./mvnw spring-boot:run
```

Abra http://localhost:8081 (a tela Angular já compilada fica em `src/main/resources/static`).

Para mexer no front com recarga automática:

```bash
cd frontend
npm install
npm start        # http://localhost:4200, chama a API na 8081
npm run build    # recompila a tela para dentro do Spring
```

## Testes

```bash
./mvnw test                  # o Chrome abre na tela
./mvnw test -Dheadless=true  # sem abrir janela
```

- `ProdutoSeleniumTest`: Teste 1 (Insert), Teste 2 (Update) e Teste 3 (Delete)
- `ProdutoServiceTest`: testes de unidade do `ProdutoService`
