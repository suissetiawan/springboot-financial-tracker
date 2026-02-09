# Setup & Installation Guide

This guide provides instructions for setting up the environment, running the application, and executing tests.

## 1. Prerequisites

- **Java 21** or higher
- **MySQL** Database
- **Redis** Server

## 2. Database Setup

Create a MySQL database (e.g., `financial_tracker`).

```sql
CREATE DATABASE financial_tracker;
```

## 3. Configuration

### Environment Variables (.env)

Duplicate the example environment file:

```bash
cp .env.example .env
```

Open `.env` and update the following values for **Docker** or **Production** runs:

- **Database Credentials** (`DB_USER`, `DB_PASSWORD`, etc.)
- **JWT Secrets** (`JWT_SECRET`, `JWT_REFRESH_SECRET`)
- **Redis Host/Port**

### Local Development Properties

For local development using the `dev` profile, update the values in:
`src/main/resources/application-dev.properties`

Ensure your local **MySQL** and **Redis** credentials match the settings in this file.

## 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port `8080`.

### Running with Profiles

You can choose between `dev`, `prod`, or `test` profiles depending on your environment.

#### 1. Using Maven

Add the `-Dspring-boot.run.profiles` argument:

```bash
# Run with dev profile (Default)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with prod profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

#### 2. Using Docker Compose

Adjust the `SPRING_PROFILES_ACTIVE` variable in your `.env` file:

```env
SPRING_PROFILES_ACTIVE=prod
```

Then restart the container:

```bash
docker compose up -d --build
```

#### 3. Using Direct Java (JAR)

If you are running the built JAR file, use `-Dspring.profiles.active`:

```bash
java -Dspring.profiles.active=prod -jar target/financial-tracker-0.0.1-SNAPSHOT.jar
```

---

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

### Coverage Report

JaCoCo is integrated to generate coverage reports. After running tests, the report can be found at:
`target/site/jacoco/index.html`

---

## Docker Support

### Prerequisites

- Docker Desktop or Docker Engine
- Docker Compose

### Running with Docker

1. Ensure you are in the project root directory.
2. Adjust the environment variables in the `.env` file as needed.
3. Run the following command:
   ```bash
   docker compose up -d --build
   ```
4. The application will be available at `http://localhost:8080`.

### Environment Variables

You can customize the configuration via the `.env` file or by setting environment variables in `docker-compose.yml`.
