# Financial Tracker Demo Scenarios

This document provides a step-by-step guide for demonstrating the Financial Tracker application. It covers both Admin flows (managing categories) and User journeys (financial transactions).

## Preparation

1.  Ensure **MySQL Database** is running.
2.  Ensure **Redis Server** is running.
3.  Start the Spring Boot application:
    ```bash
    ./mvnw spring-boot:run
    ```
4.  Prepare **Postman** or a similar tool for API testing.

---

## Scenario 0: API Discovery

_Goal: Show the root endpoint that provides API information._

- **Endpoint**: `GET /`
- **Result**: A welcome JSON response with a list of available API groups.
- **Demo Point**: The API is discoverable and provides quick links to functionality.

---

## Scenario 1: Admin Flow (Initial Setup)

_Goal: Demonstrate Role-Based Access Control and Master Data Management._

### 1. Admin Login

- **Endpoint**: `POST /auth/login`
- **Body**:
  ```json
  {
    "username": "admin",
    "password": "adminPassword"
  }
  ```
- **Result**: Copy the **Access Token** from the response. Use this token for subsequent requests (Auth Type: Bearer Token).

### 2. Create Categories (Redis Caching Demo)

- **Endpoint**: `POST /api/categories`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Body**:
  ```json
  {
    "name": "Salary",
    "type": "INCOME",
    "description": "Monthly income"
  }
  ```
- _Repeat for other categories_: "Food" (EXPENSE), "Transport" (EXPENSE).
- **Demo Point**: Explain that categories are stored in the database and will be cached in Redis.

### 3. View All Categories (Cache Hit)

- **Endpoint**: `GET /api/categories`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Action**:
  1.  First request -> Data is fetched from DB (watch console logs).
  2.  Second request -> Data is fetched from Redis (faster response).

### 4. View Users (Admin Only)

- **Endpoint**: `GET /api/users`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Result**: Displays a list of all registered users.

---

## Scenario 2: User Journey (Core Features)

_Goal: Demonstrate the main application flow from a regular user's perspective._

### 1. Register New User

- **Endpoint**: `POST /auth/register`
- **Body**:
  ```json
  {
    "username": "user_demo",
    "email": "demo@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```

### 2. User Login

- **Endpoint**: `POST /auth/login`
- **Body**: Use the new user's credentials.
- **Result**: Copy the **Access Token** for the new user.

### 3. Add Income

- **Endpoint**: `POST /api/transactions`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Body**:
  ```json
  {
    "amount": 5000000,
    "description": "January Salary",
    "category": "Salary"
  }
  ```

### 4. Add Expense

- **Endpoint**: `POST /api/transactions`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Body**:
  ```json
  {
    "amount": 50000,
    "description": "Lunch",
    "category": "Food"
  }
  ```

### 5. View Summary

- **Endpoint**: `GET /api/summary`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Result**:
  - **Total Income**: 5,000,000
  - **Total Expense**: 50,000
  - **Balance**: 4,950,000
- **Demo Point**: Shows automated balance calculation.

### 6. Transaction History

- **Endpoint**: `GET /api/transactions`
- **Header**: `Authorization: Bearer <USER_TOKEN>`
- **Result**: List of recently created transactions.

---

## Scenario 3: Security & Error Handling

_Goal: Show application robustness._

### 1. Access Without Token

- Attempt to access `GET /api/users` without Authorization header.
- **Result**: `401 Unauthorized`.

### 2. Forbidden Access

- Use a **Regular User Token** to access the Admin endpoint `GET /api/users`.
- **Result**: `403 Forbidden` (Regular users cannot view the user list).

### 3. Refresh Token

- **Endpoint**: `POST /auth/refresh`
- **Body**:
  ```json
  {
    "refreshToken": "<REFRESH_TOKEN_FROM_LOGIN>"
  }
  ```
- **Result**: Obtain a new Access Token.
