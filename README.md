# GioNails-Schedule-System

Sistema web de agendamento do **Studio Giovanna Pugliesi** (manicure): login por telefone e senha, perfis **cliente** e **manicure (administradora)**.

| Camada | Tecnologia |
|---|---|
| Frontend | Next.js 14 (App Router) + React 18 + TypeScript + Tailwind CSS |
| API | Spring Boot 3.3 (Java 21) |
| Banco | PostgreSQL 16 + Flyway (migrações versionadas) |
| Auth (etapa 2) | JWT stateless + BCrypt |

## Como rodar

```bash
# 1) variáveis de ambiente
cp .env.example .env            # edite: senha do banco, JWT_SECRET e dados da manicure
#   JWT_SECRET: openssl rand -base64 48
#   MANICURE_TELEFONE / MANICURE_SENHA: criam a conta da administradora na 1ª execução

# 2) banco
docker compose up -d

# 3) API (carrega o .env no shell e sobe o Spring Boot)
cd backend
set -a; source ../.env; set +a
mvn spring-boot:run             # http://localhost:8080/api/health

# 4) frontend (outro terminal)
cd frontend
cp ../.env.example .env.local   # só NEXT_PUBLIC_API_URL é usada aqui
npm install
npm run dev                     # http://localhost:3000
```

Testes da API: `cd backend && mvn test`

## Estrutura

```
backend/src/main/java/br/com/giovannapugliesi/agenda
  controller/  security/  exceptions/  service/
  repository/  dto/       mapper/      model/
frontend/src/app        páginas (App Router)
docs/                   decisões de arquitetura
```

## API de autenticação (etapa 2)

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/auth/register` | público | Cadastro de **cliente** (nome, telefone, senha). Já devolve o token. |
| POST | `/api/auth/login` | público | Login com telefone + senha. Devolve `{ token, tipo, expiraEm, usuario }`. |
| GET | `/api/auth/me` | autenticado | Dados do usuário logado. |
| — | `/api/admin/**` | só **MANICURE** | Reservado para o painel (etapa 5). |

Use o token no cabeçalho `Authorization: Bearer <token>`. Telefones aceitam máscara; são salvos só com dígitos e DDI 55.

## Etapas

1. ✅ Estrutura do projeto e configuração do banco
2. ✅ Autenticação por telefone e senha (cliente / manicure)
3. ⏳ Catálogo de serviços
4. ⏳ Agendamento, disponibilidade e regras de conflito
5. ⏳ Painel da manicure e tela de endereço
6. ⏳ Testes, segurança final e design

> Pagamentos aceitos pelo studio: Pix, dinheiro e cartão em até 2x (com taxa). Não há integração com gateway.
