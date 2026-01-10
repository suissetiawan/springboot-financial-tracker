# Financial Tracker API

A Spring Boot application for tracking personal finances (Income, Expenses, and Balance).

## Features

- **User Authentication**: Register, Login, Logout, Refresh Token (JWT).
- **Transactions**: Create, Read, Update, Delete transactions (Income/Expense).
- **Categories**: Organize transactions by categories.
- **Summary**: Financial summary with caching (Redis).
- **Users Management**: Admin-only access to view user lists and details.
- **Security**: Role-based access control, secure password storage, and custom error handling.

## Tech Stack

- **Java 25**
- **Spring Boot 3**
  - Spring Security (JWT)
  - Spring Data JPA
  - Spring Cache (Redis)
  - Spring Web
- **MySQL** (Database)
- **Redis** (Caching & Token Storage)
- **Lombok**

## API Endpoints

### Authentication

- `POST /auth/register`: Register a new user.
- `POST /auth/login`: Login to receive Access and Refresh tokens.
- `POST /auth/refresh`: Refresh expired Access token.
- `POST /auth/logout`: Logout and invalidate refresh token.

### Transactions

- `POST /api/transactions`: Create a transaction.
- `GET /api/transactions`: Get all transactions for the logged-in user.
- `GET /api/transactions/{id}`: Get a specific transaction.
- `PUT /api/transactions/{id}`: Update a transaction.
- `DELETE /api/transactions/{id}`: Delete a transaction.

### Categories (User read only, managed by admin)

- `GET /api/categories`: List all categories.
- `POST /api/categories`: Create a new category.
- `PUT /api/categories/{id}`: Update a category.
- `DELETE /api/categories/{id}`: Delete a category.

### Users (Admin Only)

- `GET /api/users`: Get list of all users.
- `GET /api/users/{id}`: Get details of a specific user.

### Summary

- `GET /api/summary`: Get total income, total expense, and current balance.

## Setup & Run

### 1. Prerequisites

- **Java 25** or higher
- **MySQL** Database
- **Redis** Server

### 2. Database Setup

Create a MySQL database (e.g., `financial_tracker`).

```sql
CREATE DATABASE financial_tracker;
```

### 3. Configuration

Duplicate the example configuration file:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Open `src/main/resources/application.properties` and update the following values:

- **Database Credentials**:

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/financial_tracker
  spring.datasource.username=your_db_username
  spring.datasource.password=your_db_password
  ```

- **JWT Secrets** (Use strong random strings):

  ```properties
  jwt.secret=your_super_secret_key_here
  jwt.refresh.secret=your_super_secret_refresh_key_here
  ```

- **Redis** (If running on a different host/port):
  ```properties
  spring.data.redis.host=localhost
  spring.data.redis.port=6379
  ```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port `8080`.

## Error Handling

The API returns standard JSON error responses:

```json
{
    "statusCode": 4xx/5xx,
    "message": "Error description"
}
```

- **401 Unauthorized**: Invalid or missing token.
- **403 Forbidden**: Access denied.
- **404 Not Found**: Resource not found.
- **400 Bad Request**: Validation error.
