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
- **MySQL** (Database)
- **Redis** (Caching & Token Storage)
- **Lombok**

## Documentation

For detailed information, please refer to the following guides:

- 🛠️ **[Setup & Installation Guide](docs/SETUP_GUIDE.md)**: How to get the project running locally or with Docker.
- 🚀 **[API Documentation](docs/API_DOCS.md)**: List of available endpoints and error handling.
- 🚢 **[Deployment Guide](docs/DEPLOYMENT_GUIDE.md)**: Clone, run locally, and deploy to VPS with DockerHub.
- 📊 **[Test Results](docs/TEST_RESULTS.md)**: Detailed test coverage and results summary.
- 📝 **[Demo Scenarios](docs/DEMO_SCENARIOS.md)**: Detailed usecase descriptions.

## Quick Start

If you have Docker installed, you can start the application quickly:

```bash
docker compose up -d --build
```
