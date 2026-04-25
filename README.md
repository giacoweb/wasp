# FinTrack - Personal Finance & Portfolio Tracker

University Project - HW1.

FinTrack is a server-side web application for personal finance management and investment portfolio tracking, built entirely with Java without frameworks.
It targets two distinct user groups: authenticated users who persist all their financial data in PostgreSQL, and guest users who can generate one-off financial reports.

## Team Members

- Rajmonda Bardhi
- Meviola Karriqi
- Alessandro Perin
- Smirnov Danil
- Giacomo Masiero

## Project Design & Task Management

- [Miro Board - Architecture & Design](https://miro.com/app/board/uXjVGpkBSew=/)
- [Trello Board - Task Management](https://trello.com/b/HSDd8UqV/wa2526-wasp)

## Technology Stack

* **Frontend:** JSP, CSS, JavaScript (AJAX)
* **Backend:** Java Servlets (Jakarta EE)
* **Server:** Apache Tomcat 11
* **Database:** PostgreSQL (with DAO pattern)
* **Build Tool:** Maven
* **Testing:** JUnit 5

## Dual Architecture

The application uses both architectural styles as required:

| Style | Used for | Pattern |
|-------|----------|---------|
| Servlet/JSP | Auth, Wallets, PAC Plans, Dashboard | PRG (Post/Redirect/Get) |
| REST/JSON API | Transactions, Forecasts | AJAX, proper HTTP status codes |

## Repository Structure

```
wa2526-wasp/
├── db/                              # SQL scripts for DB initialization
├── src/
│   ├── main/
│   │   ├── java/it/unipd/dei/webapp/
│   │   │   ├── config/              # Config classes
│   │   │   ├── dao/                 # DB access layer
│   │   │   ├── filter/              # Servlet filters
│   │   │   ├── listener/            # App/session listeners
│   │   │   ├── model/               # Domain entities and enums
│   │   │   ├── service/             # Business logic
│   │   │   │   └── exception/       # Custom exceptions
│   │   │   ├── servlet/
│   │   │   │   ├── api/             # REST/JSON endpoints (Transactions, Forecasts)
│   │   │   │   └── page/            # Servlet/JSP endpoints (Auth, Wallets, PAC)
│   │   │   └── util/                # Utility classes (DBConnection, PasswordUtil)
│   │   ├── resources/               # Properties and resource files
│   │   └── webapp/
│   │       ├── static/              # CSS, JS, images
│   │       ├── WEB-INF/
│   │       │   ├── jsp/             # JSP pages
│   │       │   └── web.xml
│   │       └── index.jsp
│   └── test/
│       └── java/it/unipd/dei/webapp/
│           ├── manual/              # Manual test runners (main methods)
│           └── unit/                # JUnit 5 tests
│               ├── dao/
│               ├── service/
│               └── util/
├── pom.xml
└── README.md
```

## Implemented Features

### Authentication & Sessions

- User registration and login with password hashing (PBKDF2)
- Session-based authentication with `HttpSession`
- Logout via POST and GET
- Guest session initialization with UUID token persisted in `report_sessions`
- `AuthenticationFilter` protects all private routes; returns JSON 401 for API paths, redirects to `/login` for page routes
- PRG pattern on all POST handlers

### Wallet CRUD (Servlet/JSP)

- `GET /wallets` — list all wallets for the authenticated user
- `POST /wallets/create` — create wallet with validation (3-letter currency, normalized type)
- `POST /wallets/edit` — update wallet name, type, currency
- `POST /wallets/delete` — delete wallet (rejected if wallet has linked transactions)
- Ownership verified on every mutation

### Transaction CRUD (REST/JSON API)

- `GET /api/transactions?walletId=X` — list transactions as JSON
- `POST /api/transactions` — create transaction from JSON body (201)
- `PUT /api/transactions/{id}` — update transaction (200)
- `DELETE /api/transactions/{id}` — delete transaction (204)
- Positive amount = INCOME, negative = EXPENSE (resolved at service layer)
- Amount scaled to 2 decimal places; category max 80 characters
- Request body capped at 8KB; distinct errors for malformed/empty/oversized bodies

### PAC Plan CRUD (Servlet/JSP)

- `GET /pac` — list all PAC plans
- `POST /pac/create`, `/pac/edit`, `/pac/delete` — full CRUD with PRG
- Ownership verified; asset name max 120 characters; positive amounts enforced

### Forecast Engine (REST/JSON API)

- `POST /api/forecast?scenario=X` — compute 24-month projection, stored as JSONB (201)
- `GET /api/forecast?scenario=X` — retrieve latest stored projection (200 or 404)
- Projection based on: current balance, 3-month average income/expense, recurring expenses (normalized to monthly), active PAC contributions
- Scenarios: `OPTIMISTIC` (+10% income), `CONSERVATIVE` (+3% compounding inflation), `STRESS_TEST` (-20% income, 3x expense shock at month 6)

## Database Schema

7 tables defined in `db/init.sql`:

| Table | Description |
|-------|-------------|
| `users` | User accounts with hashed passwords |
| `wallets` | Financial wallets (cash, bank, credit card, investment) |
| `transactions` | Income/expense records linked to wallets |
| `recurring_expenses` | Recurring financial obligations |
| `pac_plans` | DCA investment plans |
| `forecasts` | Scenario projections stored as JSONB |
| `report_sessions` | Guest session tokens with expiration |

5 ENUM types: `user_role`, `wallet_type`, `transaction_type`, `periodicity_type`, `forecast_scenario`

## How to Run

### 1. Configure the database

Copy the template and fill in your credentials:

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

### 2. Create the schema

```bash
psql -U your_username -d your_database -f db/init.sql
```

### 3. Build and deploy

```bash
mvn clean package
```

Deploy `target/FinTrack.war` to Tomcat 11.

### 4. Verify

- `/login` — authentication page

## Main Components

### Presentation / Web Layer

Responsible for receiving HTTP requests, returning responses/redirects, and connecting frontend pages to backend logic.

- `servlet/page/` — Servlet/JSP endpoints using PRG (Auth, Wallets, PAC)
- `servlet/api/` — REST/JSON endpoints for AJAX (Transactions, Forecasts)
- `filter/` — Request interception for authentication and guest sessions

This layer does not contain database or business logic directly.

### Service Layer

Responsible for business logic, input validation, ownership verification, and coordination between the web layer and DAO layer.

- `UserService` — registration (with PBKDF2 hashing) and login
- `WalletService` — wallet CRUD with ownership checks, currency/type normalization
- `TransactionService` — transaction CRUD with wallet ownership verification, amount/type resolution
- `PacPlanService` — PAC plan CRUD with ownership checks, field validation
- `ForecastService` — 24-month projection engine with scenario modifiers
- `GuestSessionService` — guest token creation and expiry management

### DAO Layer

Responsible for all direct database interaction using PreparedStatements and try-with-resources.

- `UserDAO` — `createUser`, `findByEmail`, `findById`
- `WalletDAO` — `findByUserId`, `findById`, `create`, `update`, `delete`, `countTransactions`
- `TransactionDAO` — `findByWalletId`, `findByUserId`, `findById`, `create`, `update`, `delete`
- `PacPlanDAO` — `findByUserId`, `findActiveByUserId`, `findById`, `create`, `update`, `delete`
- `ForecastDAO` — `save`, `findLatestByUserAndScenario`
- `RecurringExpenseDAO` — `findByUserId`, `findByWalletId`, `findById`, `create`, `delete`
- `ReportSessionDAO` — `createReportSession`, `findBySessionToken`

### Filter Layer

Responsible for centralizing access control before requests reach servlets.

- `AuthenticationFilter` — protects private routes; returns JSON 401 for API paths, redirects to `/login` for page routes
- `GuestSessionFilter` — initializes guest session tokens for `/guest/*` routes

### Utility Layer

- `DBConnection` — centralized database connection management via `config.properties`
- `PasswordUtil` — PBKDF2 password hashing and verification (pure Java, no external libraries)

## Route Protection

All protected routes are guarded by `AuthenticationFilter`:

- Page routes (`/wallets`, `/pac`, `/dashboard`, etc.) — unauthenticated requests redirect to `/login`
- API routes (`/api/*`) — unauthenticated requests return `{"error": "Authentication required."}` with HTTP 401

## Team Workflow

### Git Rules
* Single Git repository
* **Single Branch Workflow:** all work pushed directly to the main branch
* Always `git pull` before starting work
* **Commits:** Follow Conventional Commits format (`feat:`, `fix:`, `docs:`)
