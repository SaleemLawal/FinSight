-- name: CreateUser :one
INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id, name, email, created_at, updated_at;

-- name: GetUserByEmail :one
SELECT id, name, email, password FROM users WHERE email = $1;

-- name: GetUserById :one
SELECT id, name, email FROM users WHERE id = $1;

-- name: CreateAccount :one
INSERT INTO accounts (name, type, institution_name, institution_id, last_four, balance, user_id) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id, name, type, institution_name, institution_id, last_four, balance, created_at, updated_at;

-- name: GetAccountsByUserId :many
SELECT id, name, type, institution_name, institution_id, last_four, balance, created_at, updated_at FROM accounts WHERE user_id = $1;

-- name: GetAccountByIdAndUserId :one
SELECT id, name, type, institution_name, institution_id, last_four, balance, created_at, updated_at FROM accounts WHERE id = $1 AND user_id = $2;

-- name: UpdateAccount :one
UPDATE accounts SET name = $1, type = $2, institution_name = $3, institution_id = $4, last_four = $5, balance = $6 WHERE id = $7 RETURNING id, name, type, institution_name, institution_id, last_four, balance, created_at, updated_at;