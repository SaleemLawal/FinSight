# Prompt Plan for Personal Finance MVP (Copilot‑like)

> **Goal** — Ship a single‑user web app that ingests Plaid data daily, lets the user review/categorise their transactions, set budgets, and see a slick dashboard.\
> **Tech**: React (Vite + TS), Java Spring Boot (REST), PostgreSQL, Redis, Plaid API, Vercel (front) & Render (back).

---

## 0. Big‑Picture Roadmap

| Phase | Milestone                  | Why it matters                                             |
| ----- | -------------------------- | ---------------------------------------------------------- |
| 0     | **Dev & CI setup**         | Establish repeatable local + CI build, lint, format, test. |
| 1     | **Skeleton UI & API**      | App boots, Hello‑world ping, shared design system, router. |
| 2     | **Auth (Password)**        | Protect endpoints, lay DB foundation.                      |
| 3     | **Plaid Link flow**        | User links bank ➜ we get access\_token.                    |
| 4     | **Accounts sync + list**   | Surface accounts page w/ balances, types, icons.           |
| 5     | **Transactions sync**      | Cron + manual refresh, infinite‑scroll table.              |
| 6     | **Categories & Budgets**   | CRUD categories, assign to txns, monthly budgets.          |
| 7     | **Dashboard v1**           | Net‑worth & spending vs budget charts.                     |
| 8     | **Cash‑flow & Recurrings** | Detect subscriptions, income/expense trends.               |
| 9     | **Reports & polish**       | Monthly report view, settings, responsive tweaks.          |
| 10    | **Deploy & docs**          | Prod pipeline, README, runbook.                            |

Each phase closes a vertical slice that can be demoed.

---

## 1. Chunking Phases into Epics & Stories

Below, every phase is decomposed into **Epics** (▸) and smaller **Stories** (—).

### Phase 0 ▸ Dev Environment & CI

- — Initialise mono‑repo (`/frontend`, `/backend`), Prettier, ESLint, EditorConfig.
- — GitHub Actions: build, test, Docker image, codecov.
- — local `docker‑compose` (Postgres, Redis, backend).

### Phase 1 ▸ Skeleton

- — React Vite app with Tailwind, React Router, shadcn/ui.
- — Spring Boot project (`/api/v1/health`).
- — Shared API schema via OpenAPI spec.

### Phase 2 ▸ Auth

- — `users` table (id, email, pw\_hash, created\_at).
- — BCrypt signup & login endpoints.
- — JWT cookie, React context + `PrivateRoute`.

### Phase 3 ▸ Plaid Link Integration

- — Obtain Plaid sandbox keys via env.
- — Frontend Plaid Link modal; exchange `public_token` ➜ `access_token` (backend).
- — Persist `plaid_items` (item\_id, access\_token, institution, …).

### Phase 4 ▸ Accounts

- — /sync/accounts service hitting `/accounts/get`.
- — Map ➜ `accounts` table (id, item\_id, name, type, subtype, mask, balance).
- — Accounts page UI: grouped by type, toggle hide, Plaid status chip.

### Phase 5 ▸ Transactions

- — /sync/transactions (cursor‑based incremental).
- — `transactions` table + basic indices.
- — DataGrid table with filters, search, pending badge.
- — Mark‑as‑reviewed toggle.

### Phase 6 ▸ Categories & Budgets

- — `categories` (plaid\_id?, name, user\_defined).
- — Rule engine MVP (merchant → category).
- — `budgets` (category\_id, month, limit\_cents).
- — Budget progress bar component.

### Phase 7 ▸ Dashboard v1

- — `/dashboard` layout: KPI cards, net‑worth line chart, spending vs budget donut.
- — React Query hooks for cached endpoints.

### Phase 8 ▸ Cash‑flow & Recurrings

- — CRON detect recurring (Plaid `/transactions/recurring/get`).
- — `recurrings` table; upcoming list component.
- — Cash‑flow bar/area chart.

### Phase 9 ▸ Reports & Polish

- — `/reports/:month` route with summary + export PDF (later).
- — Settings page (password change, toggle accounts).
- — Lighthouse pass > 90.

### Phase 10 ▸ Deploy

- — Dockerfile prod multi‑stage.
- — Vercel + Render auto‑deploy.
- — Uptime monitor + logging.

---

## 2. Micro‑Steps (Example for Phases 0–3)

> *Pattern: one PR should tackle ****exactly one**** micro‑step.*

1. **Init repo**\
   1.1 Create GitHub repo and two folders.\
   1.2 Add LICENSE & README.\
   1.3 Enable GitHub Projects.
2. **Backend bootstrap**\
   2.1 `spring‑init` Web, JPA, Lombok.\
   2.2 Add `/health` controller returning `{status:"ok"}`.\
   2.3 Dockerfile (OpenJDK 21‑jlink).\
   2.4 `docker‑compose.yml` with Postgres.
3. **Frontend bootstrap**\
   3.1 `npm create vite@latest`.\
   3.2 Install Tailwind & shadcn presets.\
   3.3 Add `/ping` fetch to backend.
4. **CI**\
   4.1 `.github/workflows/ci.yml` build & test.\
   4.2 Codecov badge in README.
5. **Auth DB schema**\
   5.1 Flyway migration `V1__create_user.sql`.\
   5.2 User JPA entity + repository tests.
6. **Signup API**\
   6.1 Controller + service hashing pw w/ BCrypt.\
   6.2 Return JWT in http‑only cookie.
7. **Login UI**\
   7.1 Form w/ shadcn `Input` + `Button`.\
   7.2 React hook to store auth state.
8. **Plaid sandbox keys**\
   8.1 Create `.env.sample`, document secrets.\
   8.2 Backend Plaid client bean.
9. **Plaid Link modal**\
   9.1 Load Plaid script lazily.\
   9.2 Handle `onSuccess`, call `/plaid/exchange`.
10. **Store plaid item**\
    10.1 Migration `V2__plaid_items.sql`.\
    10.2 Unit test repository save.

*(Continue similarly for later phases)*

---

## 3. Code‑Gen LLM Prompt Series 🚀

Below are ready‑to‑copy prompts. Each lives in its own fenced **\`\`\`text** block and assumes the previous prompt has been executed & merged.

### Prompt 1 — Spring Boot health check

```text
You are ChatGPT coding assistant. The repo has a fresh Spring Boot 3 project named `finance-api`.
Implement an HTTP GET `/api/v1/health` endpoint returning JSON `{ "status": "ok" }` with 200.  
Constraints:
- Use a `@RestController` class `HealthController` in package `com.app.health`.
- Include unit test in `HealthControllerTest` using Spring Boot test starter.
- Ensure `gradle test` passes.
Output the full diff.
```

### Prompt 2 — Vite + Tailwind scaffold

```text
Repo root contains empty `frontend/`.  
Generate a Vite React TS project inside `frontend`, configured with Tailwind CSS and shadcn/ui.  
Add a `Ping` component that fetches `/api/v1/health` and shows the status.
Return commands + edited files necessary.
```

### Prompt 3 — Docker Compose dev stack

```text
Create a `docker-compose.yml` at repo root that starts:
1. Postgres 16 (volume `pgdata`, env `POSTGRES_PASSWORD=postgres`).
2. Redis latest.
3. finance-api built from `Dockerfile` exposing 8080.
4. frontend service using `node:20-alpine` running `npm run dev` on 5173.
All services share network `fin-net`. Include healthchecks.
```

### Prompt 4 — User Entity & Migration

```text
Add Flyway migration `V1__create_user.sql` creating table `users` (id UUID PK, email unique, pw_hash, created_at timestamptz default now()).  
Generate `User` JPA entity, repository interface, and an integration test inserting & fetching a user.
```

### Prompt 5 — BCrypt Signup API

```text
Within `AuthController` implement POST `/api/v1/auth/signup` accepting JSON `{email,pw}`.  
Hash using BCrypt (10 rounds), store row, return 201 with body `{id,email}` (no pw).  
On dup email return 409.
```

### Prompt 6 — React Signup Page

```text
Create `Signup.tsx` route using shadcn form components.  
On submit POST to `/api/v1/auth/signup`, then route to `/dashboard`.
```

### Prompt 7 — Plaid Client Bean

```text
Add dependency `com.plaid:plaid-java`.
Create `PlaidConfig` class producing a singleton `PlaidClient` bean using sandbox keys from `application.yml`.
```

### Prompt 8 — Plaid Link Flow

```text
Backend: implement POST `/api/v1/plaid/exchange` receiving `{public_token}` → exchange for `access_token` + `item_id`, save to `plaid_items` table.
Frontend: Add `LinkBankButton` that opens Plaid Link, onSuccess hits the exchange endpoint, then navigates to `/accounts`.
```

### Prompt 9 — Accounts Sync Job

```text
Create `AccountSyncService` scheduled @Daily 03:00 ET.  
Fetch `/accounts/get` for each stored item, upsert rows in `accounts` table.
Include integration test with mocked Plaid client.
```

### Prompt 10 — Accounts Page UI

```text
Add `/accounts` page displaying cards grouped by account type with balances.  
Use React Query to fetch `/api/v1/accounts`.  
Each card has eye‑slash toggle to hide account.
```

*(Generate similar prompts for remaining micro‑steps up through Phase 10)*

---

## 4. How to extend

- For every new micro‑step, duplicate the template: *"Prompt N — \<title>"* + clear constraints.
- Keep prompts < 150 lines; reference previously created files explicitly.
- After each merged PR, increment app version in `CHANGELOG.md`.

---

### Done!

This document is now the single source of truth for implementation prompts and incremental roadmap.

