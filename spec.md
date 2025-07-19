# spec.md

## Project Overview
A personal finance web app inspired by Copilot Money, designed as an MVP for a single user. It provides detailed financial tracking, budget management, and reporting through real-time bank sync using Plaid.

---

## Tech Stack
- **Frontend**: React
- **Backend**: Java Spring Boot
- **Database**: PostgreSQL
- **Hosting**: Vercel (frontend), separate host for backend (e.g., Render/Heroku)
- **Cache**: Redis (for fast access + temporary data)
- **Authentication**: Simple password-based login (no MFA or OAuth for MVP)

---

## Key Features (MVP)

### 1. Dashboard Overview
- Monthly spending vs. budget
- Asset vs. debt graph over time (1W, 1M, 3M, etc.)
- Net worth graph (assets - liabilities)
- Summary cards for accounts, spending, income

### 2. Account List by Type
- Grouped accounts: Credit Cards, Depository, Loans, Investments
- Account renaming & custom icons
- Show Plaid connection status
- Ability to hide/exclude accounts from reports/budget/net worth

### 3. Transactions
- View all transactions from Plaid
- Supports pending and cleared
- Manual editing: merchant, date, amount, category, notes
- Category assignment:
  - Use Plaid defaults
  - Manual overrides
  - Create custom categories
  - Rule-based auto-categorization (e.g., "if merchant = Uber, set to Transportation")
- Exclude specific transactions from reports (e.g., transfers, reimbursements, credit card payments)
- Transactions to Review: new/unreviewed transactions
- Search + filters (by merchant, amount, date, account, etc.)

### 4. Categories & Budgeting
- Use both Plaid default + user-defined categories
- Set total monthly budget
- Set per-category budgets (roll up to total budget)
- Budgets persist month to month, with manual adjustment per month
- Alerts for overspending
- Top Categories breakdown with actual vs. budget view

### 5. Cash Flow Tracking
- Weekly/monthly/yearly view of inflow/outflow
- Visual graphs and trends
- Income vs. expenses monthly

### 6. Recurring Charges
- Automatically detect recurring subscriptions
- Allow manual addition/editing
- Show upcoming charges on dashboard
- Notification for upcoming charges

### 7. Investments
- Display investment accounts from Plaid
- Show balances, portfolio breakdowns (e.g., ETFs, stocks, sectors)
- Performance graphs (daily, weekly, monthly)

### 8. Goals (Scoped for future iteration but structure in place)
- Set target amount + deadline
- Assign transactions or categories toward goals
- Auto-track from funding source (e.g., savings account)
- Visual progress bars

### 9. Monthly Reports
- Summary card view: income, expenses, savings, net worth change
- Accessible from Dashboard or Monthly Report tab

### 10. Sidebar Navigation
- Dashboard
- Transactions
- Categories
- Budgets
- Cash Flow
- Goals
- Recurrings
- Investments
- Accounts
- Settings/Help

### 11. Settings / Help Page
- Manage linked Plaid accounts
- Reset/update budgets and goals
- Change password
- Toggle accounts visibility in reports/budget
- View Plaid connection status + re-authenticate
- Optionally include help content or app tips

---

## Design & UI Notes
- Light mode only for MVP
- Responsive design (desktop + mobile)
- Interactive charts (hover tooltips, toggle date ranges, click to drill down)
- No onboarding wizard for now

---

## Sync & Performance
- Auto sync with Plaid daily
- Manual refresh option
- Sync on login
- Redis cache for fast loading and local state

---

## Out of Scope (for now)
- Multi-user support
- OAuth/Google/Apple login
- Tagging transactions
- Cash/manual transaction logging
- Dark mode
- Data exports (CSV/PDF)
- MFA security
- Full backup/restore features
- Flagging/favoriting transactions
- Onboarding walkthrough

