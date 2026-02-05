# Financial Tracker API

A Spring Boot application for tracking personal finances (Income, Expenses, and Balance).

## Features

- **User Authentication**: Register, Login, Logout, Refresh Token (JWT).
- **Transactions**: Create, Read, Update, Delete transactions (Income/Expense) with caching (Redis).
- **Categories**: Organize transactions by categories (Redis).
- **Summary**: Financial summary with caching (Redis).
- **Users Management**: Admin-only access to view user lists and Admin or Self details.
- **Security**: Role-based access control, secure password storage, and custom error handling.

## Tech Stack

- **Java 21**
- **Spring Boot 4**
  - Spring Security (JWT 3.0)
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
- `GET /api/transactions`: Get all transactions for the logged-in user (Redis Cache).
- `GET /api/transactions/{id}`: Get a specific transaction.
- `PUT /api/transactions/{id}`: Update a transaction.
- `DELETE /api/transactions/{id}`: Delete a transaction.

### Categories (User read only, managed by admin)

- `GET /api/categories`: List all categories (Redis).
- `POST /api/categories`: Create a new category.
- `PUT /api/categories/{id}`: Update a category.
- `DELETE /api/categories/{id}`: Delete a category.

### Users (Admin Only)

- `GET /api/users`: Get list of all users.
- `GET /api/users/{id}`: Get details of a specific user (Admin or Self).

### Summary

- `GET /api/summary`: Get total income, total expense, and current balance (Redis).

## Setup & Run

### 1. Prerequisites

- **Java 21** or higher
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
  # Expiration time in seconds
  jwt.expiration=1800
  jwt.refresh.expiration=3600
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

## Testing

The project includes both unit tests and integration tests.

### Quick Shortcuts

Use the unified test runner with optional arguments:

- **Run All Tests**: `./run-tests.sh` or `./run-tests.sh all`
- **Run Unit Tests**: `./run-tests.sh unit`
- **Run Integration Tests**: `./run-tests.sh integration`

### Run All Tests

```bash
./mvnw test
```

### Run Specific Test

```bash
./mvnw test -Dtest=AuthIntegrationTest
```

### Run Test Clusters

- **Unit Tests**:
  ```bash
  ./mvnw test -Dtest="com.mini.project.financial_tracker.service.*,com.mini.project.financial_tracker.util.*"
  ```
- **Integration Tests**:
  ```bash
  ./mvnw test -Dtest="com.mini.project.financial_tracker.integration.*"
  ```

### Coverage Report

JaCoCo is integrated to generate coverage reports. After running tests, the report can be found at:
`target/site/jacoco/index.html`

### Test Results Summary

The project maintains high code coverage for core business logic and security components.

#### Coverage Overview

| Component Group   | Coverage  | Items Tested                                                                  |
| ----------------- | --------- | ----------------------------------------------------------------------------- |
| **Services**      | 100%      | AuthService, TransactionService, CategoryService, SummaryService, UserService |
| **Utilities**     | 98.2%     | JwtUtils, SecurityUtils, JwtAuthFilter                                        |
| **Exceptions**    | 100%      | GlobalExceptionHandler, Custom Handlers, Custom Exceptions                    |
| **Total Success** | **95/95** | All tests (Unit + Integration)                                                |

#### Detailed Test Areas

- **Service Layer**: Complete validation of business rules, data persistence logic, and caching interactions.
- **Security & JWT**: Comprehensive testing of token generation, validation, and security context management.
- **Error Handling**: Detailed verification of custom exceptions and global error response formatting.
- **Integration**: End-to-end testing of Authentication flows, Transaction management, and Categories.

## Error Handling

The API returns standard JSON error responses:

```json
{
    "statusCode": 4xx/5xx,
    "message": "Error description"
}
```

- **401 Unauthorized**: Invalid or missing token / Login required.
- **403 Forbidden**: Access denied.
- **404 Not Found**: Resource not found (User, Transaction, Category).
- **400 Bad Request**: Validation error
- **500 Internal Server Error**: Server error.

## Menjalankan dengan Docker

Proyek ini sudah dilengkapi dengan konfigurasi Docker dan Docker Compose. Karena Anda menggunakan database dan Redis eksternal (container lain), pastikan layanan tersebut sudah berjalan.

### Prasyarat

- Docker Desktop atau Docker Engine
- Docker Compose
- Container MySQL & Redis yang sudah berjalan

### Langkah-langkah

1. Pastikan Anda berada di direktori root proyek.
2. Sesuaikan variabel lingkungan (`DB_HOST`, `REDIS_HOST`, dll.) agar mengarah ke container eksternal Anda. Anda bisa mengaturnya di file `.env` atau langsung di `docker-compose.yml`.
3. Jalankan perintah berikut:
   ```bash
   docker compose up -d --build
   ```
4. Aplikasi akan tersedia di `http://localhost:8080`.
5. Untuk melihat log:
   ```bash
   docker compose logs -f app
   ```
6. Untuk menghentikan layanan:
   ```bash
   docker compose down
   ```

## Variabel Lingkungan

Anda dapat menyesuaikan konfigurasi melalui file `.env` atau variabel lingkungan di `docker-compose.yml`.
