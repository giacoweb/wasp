# FinTrack - Personal Finance & Portfolio Tracker

University Project - HW1.

FinTrack is a server-side web application for personal finance management and investment portfolio tracking, built entirely with Java without frameworks.
It targets two distinct user groups: authenticated users who persist all their financial data in PostgreSQL, and guest users who can generate one-off financial reports.


### Current Phase: Sprint 1 (MVP)
Based on team discussion, our immediate focus is building a solid MVP.
Currently prioritizing:
- Core Auth (Login/Register)
- Basic CRUD for Wallets and Transactions
  *Guest features, PAC, Forecasting, and PDF exports are scheduled for later sprints.*

## Repository structure

```markdown
wa2526-wasp/
├── db/                              # SQL scripts for DB initialization
├── src/
│   ├── main/
│   │   ├── java/it/unipd/dei/webapp/
│   │   │   ├── config/              # Config classes
│   │   │   ├── dao/                 # DB access layer
│   │   │   ├── filter/              # Servlet filters
│   │   │   ├── listener/            # App/session listeners
│   │   │   ├── model/               # Domain entities
│   │   │   ├── service/             # Business logic
│   │   │   ├── servlet/
│   │   │   │   ├── api/             # JSON endpoints for AJAX
│   │   │   │   └── page/            # Servlets for JSP pages
│   │   │   └── util/                # Utility classes
│   │   ├── resources/               # Properties and resource files
│   │   └── webapp/
│   │       ├── static/              # CSS, JS, images
│   │       ├── WEB-INF/
│   │       │   ├── jsp/             # JSP pages
│   │       │   └── web.xml
│   │       └── index.jsp
│   └── test/                        # Test sources
├── pom.xml
└── README.md
```

## Technology Stack
* **Frontend:** JSP, CSS, JavaScript
* **Backend:** Java Servlets (Jakarta EE)
* **Server:** Apache Tomcat 11
* **Database:** PostgreSQL (with DAO pattern)
* **Build Tool:** Maven

## Getting Started

### 1. Database Setup (Files coming soon!)
1. Create a local PostgreSQL database named `fintrack_db`.
2. Run the DDL script located in `db/init.sql` to generate the schema.
3. Copy `config.properties.example` to `config.properties` and update it with your local credentials.

### 2. Build & Run
1. Open the project in your IDE.
2. Reload the Maven `pom.xml` to download all dependencies.
3. Configure your IDE to run the project on a local Tomcat 11 server.

## Team Workflow

### Git Rules
* We use a single Git repository.
* **Single Branch Workflow:** As per the professor's advice, we will all work and push directly to the main branch. No separate feature branches.
* **Communication is key:** Always `git pull` before starting to work, and drop a message in the group chat before a big push to avoid merge conflicts.
* **Commits:** Follow Conventional Commits format (`feat:`, `fix:`, `docs:`).cd