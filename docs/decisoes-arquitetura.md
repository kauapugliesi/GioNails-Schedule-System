# GioNails-Schedule-System · Decisões de arquitetura

## ADR-001 · Spring Boot e TypeScript
O enunciado pede API em Spring Boot e menciona TypeScript "onde couber" no backend. Spring Boot roda na JVM (Java/Kotlin) e **não executa TypeScript**; misturar um segundo runtime Node só para regras de integração adicionaria complexidade sem ganho num sistema deste porte.

**Decisão:** API 100% Spring Boot com **Java 21**. TypeScript fica no **frontend** (Next.js), incluindo o cliente HTTP tipado (`frontend/src/lib/api.ts`). Se surgir uma integração que exija Node (ex.: gateway de pagamento no futuro), ela entra como serviço separado.

## ADR-002 · Migrações com Flyway, `ddl-auto=validate`
O Hibernate **não** cria nem altera tabelas; todo o schema vem de `db/migration`. Isso dá histórico versionado e evita surpresas em produção.

## ADR-003 · Extensão `btree_gist` desde a V1
Na etapa 4, a garantia de "sem conflito de horário, mesmo com requisições concorrentes" será feita **no banco** com uma *exclusion constraint* (`EXCLUDE USING gist` sobre o intervalo do atendimento). Por isso a extensão já é habilitada na migração inicial.

## ADR-004 · Entidades nunca saem pela API
Controllers recebem/retornam apenas DTOs (`dto`); a conversão é feita por classes em `mapper`, chamadas pelo controller. `model` contém as entidades JPA.

## ADR-005 · Fuso horário
Horários de atendimento são interpretados em `America/Maceio` (`app.timezone`); persistência em UTC.

## ADR-006 · Design
Paleta extraída do catálogo da profissional (cores amostradas do PDF):
- Bege (fundo): `#E1D4C8`
- Marrom (texto/títulos): `#563F38`
- Variações derivadas para estados e superfícies: ver `frontend/tailwind.config.ts`.
Tipografia: Playfair Display (títulos) + Poppins (texto), como no catálogo.

## ADR-007 · Autenticação (etapa 2)
- Login por **telefone + senha**; senha com **BCrypt** (força 12, configurável por `BCRYPT_STRENGTH`). Mínimo de 8 e máximo de 72 caracteres (limite do BCrypt).
- **JWT stateless** (HS256, `JWT_SECRET` ≥ 32 caracteres, validade `JWT_EXPIRATION_MINUTES`, padrão 8 h). Sem refresh token nesta etapa.
- O token só carrega o id do usuário. **Perfil e status (ativo) são lidos do banco a cada requisição**, então desativar um usuário ou mudar seu perfil vale imediatamente.
- Perfis: `CLIENTE` e `MANICURE`. O cadastro público **sempre** cria `CLIENTE`. A manicure é criada na inicialização a partir de `MANICURE_NOME/TELEFONE/SENHA` (ver `ManicureInicialInitializer`), nunca por endpoint público.
- Login devolve mensagem genérica ("Telefone ou senha inválidos.") e gasta o mesmo tempo de hash quando o telefone não existe, para dificultar a descoberta de quais telefones são cadastrados.
- Erros 401/403 saem no mesmo formato `ApiError` (entry point e access-denied handler dedicados).

## ADR-008 · Campo `nome` no cadastro
O enunciado pede telefone e senha; adicionamos **nome** obrigatório porque a portaria do condomínio libera a entrada pelo nome da cliente e a manicure precisa identificar quem está na agenda.

## ADR-009 · Token no navegador
O token fica no `localStorage` para manter a cliente conectada no celular. Trade-off: legível por JavaScript em caso de XSS. Mitigações planejadas para a etapa 6: Content-Security-Policy, nenhum HTML injetado, limitação de tentativas de login (rate limit).
