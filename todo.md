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
🔐 Security & UI Development Plan
Phase 1: Critical Security Fixes (Backend) - URGENT

Step 1: Password Security 🚨 CRITICAL
- [ ] Add BCrypt dependency to pom.xml
- [ ] Create PasswordEncoder bean in configuration
- [ ] Update UserService.register() to hash passwords before saving
- [ ] Update UserService.authenticate() to use BCrypt for password verification
- [ ] Test password hashing with existing/new users

Step 2: Session Security 🚨 HIGH PRIORITY
- [ ] Add Spring Security dependency to pom.xml
- [ ] Create SecurityConfig class with session management
- [ ] Configure CSRF protection
- [ ] Set secure session cookies (HttpOnly, Secure, SameSite)
- [ ] Add session timeout configuration

Step 3: Authorization Fix 🚨 HIGH PRIORITY
- - [ ] Create authentication filter to validate user session
- - [ ] Update controllers to use authenticated user ID instead of path variable
- - [ ] Remove userId from URL paths (use session instead)
- - [ ] Update frontend to not send userId in requests

Phase 2: Basic UI Setup & Authentication
Step 4: Frontend Project Setup
- [ ] Initialize React project in frontend/ directory
- [ ] Set up TypeScript configuration
- [ ] Install essential dependencies (React Router, Axios, etc.)
- [ ] Configure proxy for API calls to backend
- [ ] Set up basic folder structure (components/, pages/, services/, etc.)

Step 5: Authentication UI Components
- - [ ] Create Login page component
- - [ ] Create Register page component
- - [ ] Create AuthContext for global auth state management
- - [ ] Implement login form with validation
- - [ ] Implement registration form with validation
- - [ ] Add error handling and loading states

Step 6: Authentication Service Integration
- [ ] Create authService.js for API calls
- [ ] Implement login API integration
- [ ] Implement register API integration
- [ ] Implement logout functionality
- [ ] Add token/session management
- [ ] Test authentication flow end-to-end

Phase 3: Core UI Components
Step 7: Dashboard Layout & Navigation
- [ ] Create main Dashboard layout component
- [ ] Implement sidebar navigation (matching spec requirements)
- [ ] Create Header component with user info
- [ ] Add protected route wrapper
- [ ] Implement responsive design basics

Step 8: Account Management UI
- [ ] Create AccountList component
- [ ] Create AccountCard component for individual accounts
- [ ] Implement "Add New Account" form
- [ ] Add account type icons and styling
- [ ] Connect to accounts API endpoints

Step 9: Basic Financial Data Display
- [ ] Create NetWorth summary component
- [ ] Create RecentTransactions placeholder component
- [ ] Add basic charts library (Chart.js or Recharts)
- [ ] Implement simple balance overview
- [ ] Style financial data cards

Phase 4: Enhanced Security & Polish

Step 10: Advanced Security Features
- [ ] Add rate limiting for login attempts
- [ ] Implement password strength validation
- [ ] Add HTTPS enforcement in production
- [ ] Create audit logging for sensitive operations
- [ ] Add request/response logging middleware

Step 11: Error Handling & Validation
- [ ] Create global error boundary for React
- [ ] Implement form validation with proper error messages
- [ ] Add loading spinners and skeleton screens
- [ ] Create toast notifications for user feedback
- [ ] Handle API error responses gracefully

Step 12: Testing & Documentation
- [ ] Add unit tests for critical security functions
- [ ] Create integration tests for auth flow
- [ ] Add React component tests for key UI elements
- [ ] Document API endpoints with proper security notes
- [ ] Create deployment security checklist

