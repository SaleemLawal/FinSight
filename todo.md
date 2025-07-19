# 📝 MVP Build Checklist

> Tick ’em off as you go. A checked box = one PR merged to **main** with green CI. 
> *Tip:* keep commits atomic and add the task code to your branch name, e.g. `feat/2‑3‑signup‑api`.

---

## Legend
- [ ] 🔲 **Open** – not started
- [~] 🛠️ **WIP** – branch pushed / PR open
- [x] ✅ **Done** – merged & deployed to dev

_(Swap the emoji as you progress — dopamine hits guaranteed ✨)_

---

- [ ] **Phase 0 – Dev & CI Setup**
  - [x] **0.1** Init mono‑repo (`frontend/`, `backend/`), LICENSE, README
  - [x] **0.2** Prettier, ESLint (TS + React rules), EditorConfig
  - [x] **0.3** Commit hooks via Husky + lint‑staged
  - [ ] **0.4** GitHub Actions ➜ build, test, codecov badge
  - [ ] **0.5** Local `docker‑compose` with Postgres & Redis
  - [ ] **0.6** Backend Dockerfile (OpenJDK 21‑jlink)
  - [ ] **0.7** CI passes on fresh clone (`make ci`)

- [ ] **Phase 1 – Skeleton UI & API**
  - [ ] **1.1** Spin up Vite + React TS app
  - [ ] **1.2** Add Tailwind + shadcn/ui + global theme
  - [ ] **1.3** Set up React Router (public + private shells)
  - [ ] **1.4** Spring Boot base project; `/api/v1/health` endpoint
  - [ ] **1.5** Shared OpenAPI contract (generate TS client)
  - [ ] **1.6** `Ping` component calling health endpoint

- [ ] **Phase 2 – Auth (Password)**
  - [ ] **2.1** Flyway `users` table migration
  - [ ] **2.2** `User` JPA entity + repository tests
  - [ ] **2.3** `AuthController` POST `/signup` (BCrypt hash)
  - [ ] **2.4** `AuthController` POST `/login` (JWT cookie)
  - [ ] **2.5** React auth context + `PrivateRoute`
  - [ ] **2.6** Signup & Login forms with validation (zod)

- [ ] **Phase 3 – Plaid Link Flow**
  - [ ] **3.1** Add Plaid sandbox keys to secrets
  - [ ] **3.2** Backend Plaid client bean + config tests
  - [ ] **3.3** `plaid_items` migration & entity
  - [ ] **3.4** POST `/plaid/exchange` save access_token
  - [ ] **3.5** Frontend `LinkBankButton` w/ onSuccess handler
  - [ ] **3.6** Accounts placeholder page after link

- [ ] **Phase 4 – Accounts Sync + List**
  - [ ] **4.1** `AccountSyncService` scheduled daily
  - [ ] **4.2** `accounts` table migration & entity
  - [ ] **4.3** GET `/accounts` endpoint w/ DTO mapping
  - [ ] **4.4** Accounts page: cards grouped by type, eye‑slash hide
  - [ ] **4.5** E2E test: link sandbox + list accounts

- [ ] **Phase 5 – Transactions Sync**
  - [ ] **5.1** `transactions` table migration (indices!)
  - [ ] **5.2** Incremental sync via `/transactions/sync`
  - [ ] **5.3** GET `/transactions` with pagination & filters
  - [ ] **5.4** Infinite‑scroll DataGrid w/ React Query
  - [ ] **5.5** Mark‑as‑reviewed checkbox (PATCH)

- [ ] **Phase 6 – Categories & Budgets**
  - [ ] **6.1** `categories` & `budgets` migrations
  - [ ] **6.2** Rule engine MVP (merchant → category)
  - [ ] **6.3** Category CRUD UI (modal)
  - [ ] **6.4** Budget CRUD + progress bar

- [ ] **Phase 7 – Dashboard v1**
  - [ ] **7.1** Net‑worth over time line chart
  - [ ] **7.2** Spending vs budget donut / bar
  - [ ] **7.3** Top merchants list (last 30 days)

- [ ] **Phase 8 – Cash‑flow & Recurrings**
  - [ ] **8.1** Detect recurring transactions (Plaid API)
  - [ ] **8.2** `recurrings` table & upsert logic
  - [ ] **8.3** Cash‑flow forecast chart
  - [ ] **8.4** Subscriptions list with cancel link placeholder

- [ ] **Phase 9 – Reports & Polish**
  - [ ] **9.1** Monthly report route `/reports/:yyyy-mm`
  - [ ] **9.2** Export PDF (html‑pdf)
  - [ ] **9.3** Settings page (pw change, hide accounts)
  - [ ] **9.4** Responsive / Lighthouse > 90

- [ ] **Phase 10 – Deploy & Docs**
  - [ ] **10.1** Multi‑stage prod Dockerfile
  - [ ] **10.2** Render (backend) + Vercel (frontend) pipelines
  - [ ] **10.3** Post‑deploy smoke tests
  - [ ] **10.4** UptimeRobot monitor + log aggregation
  - [ ] **10.5** `RUNBOOK.md` & updated README badges

---

### Scratchpad
Use this for ad‑hoc todos, ideas, or hotfix notes.
- [ ] …

