# Agenda · Studio Giovanna Pugliesi

Sistema web de agendamento para manicure: login por telefone e senha, perfis **cliente** e **manicure (administradora)**.

| Camada | Tecnologia |
|---|---|
| Frontend | Next.js 14 (App Router) + React 18 + TypeScript + Tailwind CSS |
| API | Spring Boot 3.3 (Java 21) |
| Banco | PostgreSQL 16 + Flyway (migrações versionadas) |
| Auth (etapa 2) | JWT stateless + BCrypt |

## Como rodar (etapa 1)

```bash
# 1) variáveis de ambiente
cp .env.example .env            # edite a senha do banco

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

## Etapas

1. ✅ Estrutura do projeto e configuração do banco
2. ⏳ Autenticação por telefone e senha (cliente / manicure)
3. ⏳ Catálogo de serviços
4. ⏳ Agendamento, disponibilidade e regras de conflito
5. ⏳ Painel da manicure e tela de endereço
6. ⏳ Testes, segurança final e design

> Pagamentos aceitos pelo studio: Pix, dinheiro e cartão em até 2x (com taxa). Não há integração com gateway.
