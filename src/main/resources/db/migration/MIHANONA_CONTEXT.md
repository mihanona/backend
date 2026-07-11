# MIHANONA — New Conversation Context
## Read this FIRST at the start of every new conversation
**Last updated: June 2026 | Use this to resume work without repeating history**

---

## WHO I AM

I am Badr Eddine — a developer learning by building a real SaaS product called **Mihanona**.
I need you to be my **senior engineer mentor**: teach me while we build, explain every concept,
never skip the "why", and always help me work like a professional.

**My skill level:**
- Angular: Beginner (first real app)
- Spring Boot: Basic (first production-grade project)
- Docker: Installed, ran first containers
- PostgreSQL: Basic SQL knowledge
- Redis: Never used before
- Flyway: Never used before
- JWT/Security: Conceptual understanding, never implemented
- GitHub Projects: Just started using
- Testing: Know what it is, never written seriously

**How I learn best:** Explain concept → why it exists → real example → then code.
Never give me code without explaining it first.

---

## WHAT MIHANONA IS

**Field Service Management SaaS** — the Jobber of the Arab world.

Target: Service businesses in Morocco + MENA (plumbers, electricians, AC techs,
cleaning companies, maintenance firms) with 2–20 employees.

Current workflow they use: WhatsApp + Excel + paper = chaos.
What Mihanona replaces: the entire operation from first client call to final payment.

**The workflow:** Client → Request → Quote (Devis) → Job → Invoice (Facture) → Payment

**Languages:** Arabic + French + English (NO Darija in the app ever)

**Positioning:** NOT an invoice generator. NOT an ERP. A workflow platform.
Competitors: Fatoora.app (too simple), Daftra (too heavy), Jobber (English only, no MENA).

**Pricing:** 14-day free trial (no free plan) → 99/299/599 MAD/month

---

## TECH STACK (FINAL — DO NOT CHANGE)

### Backend
- Java 21 LTS
- Spring Boot **3.5.14** (NOT 4.x — too many breaking changes for a beginner)
- Spring Security 6 + JWT (JJWT 0.12.7)
- Spring Data JPA + Hibernate
- PostgreSQL 17
- Redis 7
- Flyway 10.x (migrations — ddl-auto: validate ALWAYS)
- MapStruct 1.6.3
- Lombok
- Spring Mail + Thymeleaf (emails)

### Frontend
- Angular **21** (standalone components, Signals — NO NgRx)
- TypeScript 5.7+
- Tailwind CSS v4 (NO config file in v4)
- Lucide Angular (icons)
- TanStack Query for Angular (server state)
- **NO Angular Material** — custom components with Tailwind only

### Tools
- IntelliJ IDEA (backend)
- VS Code (frontend)
- Bruno (API testing — test EVERY endpoint before building Angular UI)
- Docker Desktop
- DBeaver (visual DB browser)
- GitHub org: github.com/mihanona (repos: backend + frontend)
- GitHub Projects board: 5 columns (Backlog → This Sprint → In Progress → Review/Test → Done)

---

## CURRENT STATUS — WHERE WE ARE

### ✅ DONE
1. Product vision, market research, positioning
2. DB schema: 22 tables (complete, reviewed)
3. API contract: 9 modules, ~40 endpoints (complete)
4. Design system: Jobber-inspired, warm cream bg, forest green primary
5. Spring Boot 3.5.14 project created and builds successfully
6. docker-compose.dev.yml created
7. application.yml configured
8. Redis + MailDev containers running
9. GitHub org + repos + Projects board created
10. All 28 Sprint 1 tickets created in GitHub Projects

### 🔴 IMMEDIATE PROBLEM — FIX THIS FIRST
**PostgreSQL is NOT running.** Only Redis and MailDev show in `docker ps`.

Fix:
```bash
cd mihanona-backend
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.dev.yml up -d
docker ps
# Must show ALL 3: mihanona_postgres + mihanona_redis + mihanona_mail
```

If postgres fails, check: `docker logs mihanona_postgres`

### ⬜ NEXT TASK (after fixing Postgres)
**Ticket 4:** Verify Spring Boot starts and connects to all services.
Then **Ticket 5:** Create shared foundation classes (BaseEntity, ApiResponse, TenantContext, GlobalExceptionHandler).

---

## PROJECT FILE STRUCTURE

```
mihanona-backend/                    ← IntelliJ project
├── src/main/java/com/mihanona/backend/
│   ├── BackendApplication.java
│   ├── shared/                      ← NEXT: create these 4 classes
│   │   ├── entity/BaseEntity.java
│   │   ├── response/ApiResponse.java
│   │   ├── security/TenantContext.java
│   │   └── exception/GlobalExceptionHandler.java
│   ├── auth/                        ← After shared classes
│   ├── tenant/
│   ├── user/
│   ├── client/
│   ├── catalog/
│   ├── invoice/
│   │   ├── devis/
│   │   ├── facture/
│   │   └── sequence/
│   ├── payment/
│   └── dashboard/
├── src/main/resources/
│   ├── application.yml              ← DONE
│   └── db/migration/                ← NEXT: create V1__create_tenant_user.sql
├── docker-compose.dev.yml           ← DONE
└── pom.xml                         ← DONE (Spring Boot 3.5.14)

mihanona-frontend/                   ← NOT STARTED yet (Sprint 1 Epics 6-7)
```

---

## DOCKER COMPOSE (what it should look like)

File: `mihanona-backend/docker-compose.dev.yml`

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:17-alpine
    container_name: mihanona_postgres
    environment:
      POSTGRES_DB: mihanona_db
      POSTGRES_USER: mihanona_user
      POSTGRES_PASSWORD: mihanona_local
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD", "pg_isready", "-U", "mihanona_user", "-d", "mihanona_db"]
      interval: 10s
      timeout: 5s
      retries: 5
  redis:
    image: redis:7-alpine
    container_name: mihanona_redis
    command: redis-server --requirepass redis_local
    ports:
      - "6379:6379"
  maildev:
    image: maildev/maildev
    container_name: mihanona_mail
    ports:
      - "1080:1080"
      - "1025:1025"
volumes:
  postgres_data:
```

---

## APPLICATION.YML (current working config)

```yaml
spring:
  application:
    name: mihanona-backend
  datasource:
    url: jdbc:postgresql://localhost:5432/mihanona_db
    username: mihanona_user
    password: mihanona_local
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false
  data:
    redis:
      host: localhost
      port: 6379
      password: redis_local
  mail:
    host: localhost
    port: 1025
    username: ""
    password: ""
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false
server:
  port: 8080
  servlet:
    context-path: /api/v1
app:
  jwt:
    secret: mihanona-dev-secret-key-minimum-32-characters-long
    access-token-expiry: 900
    refresh-token-expiry: 604800
```

---

## POM.XML KEY DETAILS

- Spring Boot: 3.5.14
- Java: 21
- JJWT: 0.12.7
- MapStruct: 1.6.3 (with annotation processor — Lombok BEFORE MapStruct)
- Single test dependency: `spring-boot-starter-test` + `spring-security-test`
- flyway-core + flyway-database-postgresql (NOT spring-boot-starter-flyway)
- spring-boot-starter-web (NOT spring-boot-starter-webmvc)

---

## SPRINT 1 — 28 TICKETS STATUS

### Epic 1 — Foundation
- [x] Ticket 1: Create Spring Boot project ✅ DONE
- [⚠] Ticket 2: Docker Compose — partial (Postgres not running)
- [x] Ticket 3: application.yml configured ✅ DONE
- [ ] Ticket 4: Verify Spring Boot connects to all services

### Epic 2 — Database Migrations
- [ ] Ticket 5: V1 — tenant + user tables
- [ ] Ticket 6: V2 — tokens
- [ ] Ticket 7: V3 — sequences
- [ ] Ticket 8: V4-V5 — clients + services
- [ ] Ticket 9: V6-V12 — all remaining

### Epic 3 — Shared Classes
- [ ] Ticket 10: BaseEntity.java
- [ ] Ticket 11: ApiResponse.java
- [ ] Ticket 12: TenantContext.java (MOST IMPORTANT — security)
- [ ] Ticket 13: GlobalExceptionHandler.java

### Epic 4 — Auth Endpoints
- [ ] Ticket 14: Tenant.java + User.java entities
- [ ] Ticket 15: POST /auth/register
- [ ] Ticket 16: Spring Security + JWT filter
- [ ] Ticket 17: POST /auth/login + verify-email
- [ ] Ticket 18: POST /auth/refresh + logout

### Epic 5 — Test Auth
- [ ] Ticket 19: All auth in Bruno — RULE: NO Angular before this is done

### Epic 6 — Angular Foundation
- [ ] Ticket 20-24: Angular setup, tokens.scss, layouts, AuthService

### Epic 7 — Auth Screens
- [ ] Ticket 25-28: Login, Register, Verify email, E2E test 🎉

---

## THE GOLDEN RULES (repeat every session)

1. **Backend first. Test in Bruno. Then Angular.** — Never build UI before endpoint works.
2. **One ticket at a time.** Move to In Progress only when actively working.
3. **Every query must include tenantId.** Never `findById(id)` alone.
4. **Amount due is never stored.** Always computed: `total_amount - amount_paid`.
5. **Flyway owns the schema.** `ddl-auto: validate` means Spring never touches tables.
6. **Commit after every working thing.** Format: `feat(auth): implement register endpoint`
7. **Log what you learn** in the Notion Learning Log after every session.

---

## NOTION WORKSPACE LINKS

- 🏠 HQ Hub: https://app.notion.com/p/37aca4967c9081cdbc8ed1b2302e8877
- 🎯 Product Vision: https://app.notion.com/p/37bca4967c908175b4c7f572e73e2e12
- 🏗️ Architecture: https://app.notion.com/p/37bca4967c90814a8e10ca2806691195
- 🗄️ DB Schema: https://app.notion.com/p/37bca4967c9081ef9db6ccc6ef06982f
- 📋 API Contract: https://app.notion.com/p/37bca4967c9081899023d8a768386f9c
- 🎨 Design System: https://app.notion.com/p/37bca4967c90814ca7f5e64b6039b0d4
- 🚀 Sprint Tracker: https://app.notion.com/p/37bca4967c908187b9c1edbd9aa76902
- 📚 Learning Log: https://app.notion.com/p/37cca4967c908193bec6e334cc9b4ee5
- 🐛 Known Issues: https://app.notion.com/p/37cca4967c90819297b7fcb1f0072ce6

## GITHUB LINKS
- Org: https://github.com/mihanona
- Backend: https://github.com/mihanona/backend
- Frontend: https://github.com/mihanona/frontend
- Projects Board: https://github.com/orgs/mihanona/projects/1

## OTHER REFERENCES
- DB diagram: https://dbdiagram.io/d/Khdami-6a031ca854a51d93d3055438
- Jobber (design inspiration): https://secure.getjobber.com

---

## HOW TO START A NEW CONVERSATION

Paste this at the top of every new chat:

---
I am building **Mihanona** — a Field Service Management SaaS (the Jobber of the Arab world).
Please read the context file I am about to paste and then help me continue from where I left off.
You are my senior engineer mentor. Teach me while we build. One concept at a time.

[paste this entire MIHANONA_CONTEXT.md file]

The immediate task is: [describe what you're working on]
---
