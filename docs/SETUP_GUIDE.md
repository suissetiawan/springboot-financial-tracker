# Setup & Installation Guide

This guide provides instructions for setting up the environment, running the application, and executing tests.

## 1. Prerequisites

- **Java 21** or higher
- **MySQL** Database
- **Redis** Server
- **Docker** & **Docker Compose** (Optional, for running dependencies easily)

## 2. Database Setup

### Option A: Using Docker (Recommended)

You can spin up MySQL and Redis quickly using Docker Compose:

```bash
docker compose up -d mysql redis
```

### Option B: Manual Setup

1. Install MySQL and Redis.
2. Create a database:

```sql
CREATE DATABASE financial_tracker;
```

## 3. Configuration

### Environment Variables (.env)

Duplicate the example environment file:

```bash
cp .env.example .env
```

Open `.env` and update the values. For local development, the defaults usually work if you used the Docker setup above.

### Local Development Properties

For local development using the `dev` profile, you can also check:
`src/main/resources/application-dev.properties`

## 4. Run the Application

### Method 1: Using Maven (Recommended for Dev)

```bash
# Run with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080`.

### Method 2: High-Fidelity Local Test (Docker)

To test the production-like container locally:

```bash
# Build and run everything
docker compose up -d --build
```

Access at `http://localhost:8080`.

---

## Testing

The project includes both unit tests and integration tests.

### Quick Shortcuts

Use the unified test runner:

- **Run All Tests**: `./run-tests.sh`
- **Run Unit Tests**: `./run-tests.sh unit`
- **Run Integration Tests**: `./run-tests.sh integration`

### Manual Maven Commands

```bash
./mvnw test
```

### Coverage Report

JaCoCo is integrated to generate coverage reports at:
`target/site/jacoco/index.html`

> 🚀 **Ready to Deploy?**
>
> Once you have tested locally, check the **[Deployment Guide](DEPLOYMENT_GUIDE.md)** for production deployment instructions.
