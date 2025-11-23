# FinSight API Documentation

## Base URL

```
http://localhost:8080/api
```

**Note:** Update the `baseUrl` variable in Postman if your server runs on a different port.

---

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

The token is automatically saved to the `authToken` environment variable after a successful login.

---

## Endpoints

### Health Check

#### `GET /healthz`

Check if the API is running and healthy.

**No authentication required**

**Response:**

```json
{
  "message": "API is live ✅",
  "data": null
}
```

**Status Code:** `200 OK`

---

### User Endpoints

#### `POST /user`

Register a new user account.

**No authentication required**

**Request Body:**

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Success Response:**

```json
{
  "message": "User registered successfully",
  "data": null
}
```

**Status Code:** `200 OK`

**Error Responses:**

- `400 Bad Request` - Invalid request body
  ```json
  {
    "error": "Invalid request body",
    "code": "INVALID_REQUEST_BODY"
  }
  ```
- `409 Conflict` - Email already exists
  ```json
  {
    "error": "Email already exists",
    "code": "EMAIL_ALREADY_EXISTS"
  }
  ```

---

#### `POST /user/login`

Login with email and password.

**No authentication required**

**Request Body:**

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Success Response:**

```json
{
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com"
    }
  }
}
```

**Status Code:** `200 OK`

**Error Responses:**

- `400 Bad Request` - Missing email or password
  ```json
  {
    "error": "Email and password are required",
    "code": "INVALID_REQUEST"
  }
  ```
- `401 Unauthorized` - Invalid password
  ```json
  {
    "error": "Invalid password",
    "code": "INVALID_PASSWORD"
  }
  ```
- `404 Not Found` - User not found
  ```json
  {
    "error": "User not found",
    "code": "USER_NOT_FOUND"
  }
  ```

---

#### `GET /user`

Get the currently authenticated user's information.

**Authentication required**

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Success Response:**

```json
{
  "message": "Current user retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

**Status Code:** `200 OK`

**Error Responses:**

- `401 Unauthorized` - Missing or invalid token
  ```json
  {
    "error": "Unauthorized",
    "code": "UNAUTHORIZED"
  }
  ```
- `404 Not Found` - User not found
  ```json
  {
    "error": "User not found",
    "code": "USER_NOT_FOUND"
  }
  ```

---

#### `POST /user/logout`

Logout the current user.

**Authentication required**

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Success Response:**

```json
{
  "message": "Logout successful",
  "data": null
}
```

**Status Code:** `200 OK`

**Note:** Currently returns success but session invalidation is TODO.

**Error Responses:**

- `401 Unauthorized` - Missing or invalid token
  ```json
  {
    "error": "Unauthorized",
    "code": "UNAUTHORIZED"
  }
  ```

---

## Response Format

### Success Response

All successful responses follow this format:

```json
{
    "message": "Success message",
    "data": <response-data>
}
```

### Error Response

All error responses follow this format:

```json
{
  "error": "Error message",
  "code": "ERROR_CODE"
}
```

---

## Postman Collection

Import the `FinSight_API.postman_collection.json` file into Postman for quick testing:

1. Open Postman
2. Click **Import** button
3. Select `FinSight_API.postman_collection.json`
4. The collection will be imported with all endpoints pre-configured

### Environment Variables

The collection uses the following variables:

- `baseUrl` - Base URL for the API (default: `http://localhost:8080`)
- `authToken` - JWT token (automatically set after login)

To update the base URL:

1. Click on the collection name
2. Go to the **Variables** tab
3. Update the `baseUrl` value

### Auto-Save Token

The Login endpoint automatically saves the JWT token to the `authToken` environment variable, so authenticated requests will work immediately after logging in.
