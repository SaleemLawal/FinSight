CREATE TABLE IF NOT EXISTS users (
    -- Change to UUID later
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    -- enum for account type := CREDIT, DEPOSITORY, INVESTMENT, LOAN
    type VARCHAR(255) NOT NULL CHECK (type IN ('CREDIT', 'DEPOSITORY', 'INVESTMENT', 'LOAN')),
    institution_name VARCHAR(255) NOT NULL,
    institution_id VARCHAR(255) NOT NULL,
    last_four VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    -- reference key to users table
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- ensure unique accounts per user based on institution and account identifier
    UNIQUE (user_id, last_four, institution_id)
);

CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);