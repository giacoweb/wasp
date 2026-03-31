CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TYPE user_role AS ENUM ('REGISTERED', 'GUEST_SESSION');
CREATE TYPE periodicity_type AS ENUM ('MONTHLY', 'QUARTERLY', 'YEARLY');
CREATE TYPE forecast_scenario AS ENUM ('OPTIMISTIC', 'CONSERVATIVE', 'STRESS_TEST');
CREATE TYPE wallet_type AS ENUM ('CASH', 'BANK_ACCOUNT', 'CREDIT_CARD', 'INVESTMENT');

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(100),
                       surname VARCHAR(100),
                       email VARCHAR(255) UNIQUE,
                       password_hash VARCHAR(255),
                       role user_role NOT NULL DEFAULT 'REGISTERED',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallets (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         name VARCHAR(100) NOT NULL,
                         type wallet_type NOT NULL,
                         currency CHAR(3) NOT NULL DEFAULT 'EUR',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              wallet_id UUID NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                              amount DECIMAL(12,2) NOT NULL,
                              category VARCHAR(80) NOT NULL,
                              date DATE NOT NULL,
                              note TEXT,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recurring_expenses (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    wallet_id UUID NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                                    label VARCHAR(120) NOT NULL,
                                    amount DECIMAL(12,2) NOT NULL,
                                    periodicity periodicity_type NOT NULL,
                                    next_due DATE NOT NULL,
                                    active BOOLEAN DEFAULT TRUE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pac_plans (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           scenario forecast_scenario NOT NULL,
                           projection_json JSONB NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE forecasts (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           session_token UUID UNIQUE NOT NULL,
                           report_json JSONB NOT NULL,
                           expires_at TIMESTAMP NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE report_sessions (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 asset_name VARCHAR(120) NOT NULL,
                                 monthly_amount DECIMAL(12,2) NOT NULL,
                                 target_amount DECIMAL(14,2) NOT NULL,
                                 start_date DATE NOT NULL,
                                 active BOOLEAN DEFAULT TRUE,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);