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


## Overview (Current Authentication Stage)

At the current stage, the project includes the initial authentication and session flow for both registered users and guests.

Implemented features:

- user registration
- user login
- user logout
- password hashing with BCrypt
- separation of public and private routes
- authentication filter for protected pages
- guest session initialization
- session token persistence in `report_sessions`
- landing page with:
  - Log in
  - Register
  - Continue as Guest

This README explains:

- which pages/routes currently exist
- how the application is structured
- how the authentication flow works
- how to run and test the current version

---

## Current Pages / Routes

### Public pages

These pages can be accessed without authentication.

#### `/`
Landing page.

Purpose:
- entry point of the application
- allows the user to choose between:
  - Log in
  - Register
  - Continue as Guest

#### `/login`
Login page.

Purpose:
- allows an existing user to authenticate using email and password

#### `/register`
Registration page.

Purpose:
- allows a new user to create an account

#### `/ping`
Temporary backend check endpoint.

Purpose:
- simple route to verify that the backend is running and reachable

---

### Protected / private pages

These pages are intended only for authenticated users or for flows allowed by the current session logic.

Access is controlled by `AuthenticationFilter`.

Depending on the current implementation, private pages are accessible only when the request contains a valid authenticated session.

---

## Authentication Flow

## 1. Registration

When a user registers:

1. the user opens the registration page
2. submits email and password
3. the POST handler receives the form data
4. the data is passed to the service layer
5. the password is hashed with BCrypt
6. the user is inserted into the database
7. after success, the application redirects according to the configured flow

Notes:
- raw passwords are not stored in the database
- password hashing is performed before persistence

---

## 2. Login

When a user logs in:

1. the user opens the login page
2. submits email and password
3. the POST handler passes the request to the service layer
4. the service loads the user through the DAO layer
5. the entered password is checked against the stored BCrypt hash
6. if valid, an authenticated session is created
7. the user is redirected to the private area

---

## 3. Logout

When a user logs out:

1. the current session is invalidated
2. authentication/session data is cleared
3. the user is redirected to a public page

---

## 4. Guest Flow

When a user selects **Continue as Guest**:

1. a guest session is initialized
2. a token/session entry is created
3. the token is persisted in the `report_sessions` table
4. the guest can continue through the application according to the current session rules

Purpose:
- allows using the application without a registered account
- still provides a persistent session identity for tracking/report-related logic

---

## Route Protection

Private routes are protected by `AuthenticationFilter`.

The filter works as follows:

1. intercepts incoming HTTP requests
2. checks whether the requested route is public or protected
3. verifies whether the session is valid for access
4. if access is allowed, the request continues
5. otherwise, the user is redirected to an appropriate public page

Why this is useful:
- access control logic is centralized
- authentication checks do not need to be repeated in every servlet
- future protected pages can be added more easily

---

## PRG Pattern

POST actions such as login and registration use the **Post/Redirect/Get** pattern.

Flow:

1. browser sends a POST request
2. server processes the request
3. server responds with a redirect
4. browser loads the next page using GET

Why this is used:
- prevents accidental duplicate form submissions
- avoids browser resubmission warnings on refresh
- keeps the navigation flow cleaner

---

## Project Structure

The current implementation follows a layered structure.

### 1. Presentation / Web layer

Responsible for:
- receiving HTTP requests
- returning responses / redirects
- handling form submissions
- connecting frontend pages to backend logic

Typical contents:
- servlets
- route handlers
- JSP/pages

This layer should not contain database logic directly.

---

### 2. Service layer

Responsible for:
- business logic
- validation
- authentication-related decisions
- coordination between web layer and DAO layer

Example responsibilities:
- validate input
- call DAO methods
- decide whether login succeeds
- manage higher-level auth flow

This layer exists to avoid putting all logic into servlets.

---

### 3. DAO layer

Responsible for:
- all direct database interaction
- SQL queries
- insert/select/update operations

Example:
- `UserDAO` reads or writes user-related data

This keeps database logic isolated from business logic.

---

### 4. Filter layer

Responsible for:
- checking access before the request reaches protected routes

Example:
- `AuthenticationFilter`

This is where route protection is centralized.

---

## Main Components

### `UserDAO`

Purpose:
- handles user-related database operations

Typical responsibilities:
- insert a new user
- find a user by email
- retrieve data needed for authentication

---

### `UserService`

Purpose:
- contains authentication and user-related business logic

Typical responsibilities:
- validate registration/login input
- call DAO methods
- compare passwords using BCrypt
- coordinate session/auth behavior

---

### `AuthenticationFilter`

Purpose:
- protect private routes

Typical responsibilities:
- determine whether the route requires authentication
- check session state
- allow or block the request

---

### `DBConnection` (temporary / test utility)

Purpose:
- simplify connection testing and development at the current stage

Note:
- this may later be refactored depending on the final architecture

---

### `/ping`

Purpose:
- quick backend availability check

Recommended usage:
- open `/ping` after deployment/startup to verify that the application is alive

---

## Database Notes

At the current stage, the authentication/session flow relies on:

- user-related data stored in the users table
- guest/session-related token persistence in `report_sessions`

Passwords are stored as BCrypt hashes, not as plain text.

Before running the project, make sure:
- the database is running
- connection properties are correct
- required tables exist

---

## How to Run

## 1. Configure the database

Set the correct database connection values in the properties/configuration file.

Make sure:
- database host is correct
- port is correct
- database name is correct
- username/password are correct

---

## 2. Create the required tables

Ensure that the tables used by the current authentication/session logic already exist.

At minimum, this includes:
- users-related table
- `report_sessions`

---

## 3. Start the application server

Deploy and run the web application in the configured server/container.

---

## 4. Open the application

Start from:

- `/` for the landing page
- `/ping` to verify backend status

---

## 5. Test the main flows

Recommended manual test sequence:

### Registration test
1. open `/register`
2. create a new user
3. verify that the user is inserted into the database
4. verify that the stored password is hashed

### Login test
1. open `/login`
2. log in with valid credentials
3. verify redirect to protected area
4. verify that invalid credentials are rejected

### Logout test
1. log in
2. log out
3. verify that the session is invalidated
4. verify that protected routes are no longer accessible

### Guest test
1. open `/`
2. choose **Continue as Guest**
3. verify that guest session/token is created
4. verify persistence in `report_sessions`

### Filter test
1. try to open a protected page without authentication
2. verify that access is blocked or redirected
3. log in
4. verify that the same page becomes accessible

---

## Why the Project Is Structured This Way

This structure was chosen to keep responsibilities separated and make the code easier to maintain.

Benefits:

- servlets stay focused on HTTP handling
- business logic is not mixed with SQL
- database logic is isolated in DAO classes
- route protection is centralized in a filter
- future refactoring is easier
- future team members can understand where each type of logic belongs

In practice, this means:

- if validation changes, update the service layer
- if SQL changes, update the DAO layer
- if access rules change, update the filter
- if page flow changes, update the web layer

This avoids duplicating logic across the project.

---

## Temporary / Development Notes

The current branch still contains some temporary/test-oriented parts, including:

- `DBConnection`
- `UserDAO`
- `UserService`
- `/ping`

These were intentionally left in place to make the current stage easier to test and understand.

They can be cleaned up or refactored later once the team aligns on the final architecture.

---

## Branch Information

Current implementation is pushed to:

`feature/user-authentication`

It has **not** been merged into `master` yet.

---

## Suggested Next Steps

Possible next improvements:

- refine naming and package organization
- standardize route naming
- improve validation and error messages
- add clearer session handling documentation
- remove or refactor temporary test classes
- add unit/integration tests
- document exact database schema used by auth/session flow

---

## Quick Summary

At this stage, the project already provides:

- registration
- login/logout
- secure password hashing
- guest entry flow
- private/public route separation
- centralized authentication filtering
- session persistence support for guests
- basic backend health check

This is the current foundation for future protected features and user-specific flows.

## Team Workflow

### Git Rules
* We use a single Git repository.
* **Single Branch Workflow:** As per the professor's advice, we will all work and push directly to the main branch. No separate feature branches.
* **Communication is key:** Always `git pull` before starting to work, and drop a message in the group chat before a big push to avoid merge conflicts.
* **Commits:** Follow Conventional Commits format (`feat:`, `fix:`, `docs:`).cd