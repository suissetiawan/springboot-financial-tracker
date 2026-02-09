# Deployment Guide

This guide covers how to clone and run the Financial Tracker API locally, and how to deploy it to a VPS using DockerHub and GitHub Actions.

---

## Quick Start (Local Development)

### 1. Clone the Repository

```bash
git clone https://github.com/suissetiawan/springboot-financial-tracker.git
cd springboot-financial-tracker
```

### 2. Prerequisites

Ensure you have the following installed:

- **Java 21** or higher
- **MySQL** Database
- **Redis** Server
- **Maven** (or use the included `./mvnw` wrapper)

### 3. Configure Environment

Update database and Redis credentials in:
`src/main/resources/application-dev.properties`

### 4. Create Database

```sql
CREATE DATABASE financial_tracker;
```

### 5. Run the Application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at `http://localhost:8080`.

> For detailed setup instructions, see the **[Setup & Installation Guide](SETUP_GUIDE.md)**.

---

## Production Deployment to VPS

This section covers deploying the application to a VPS using Docker and automated CI/CD with GitHub Actions.

### Prerequisites

On your VPS, ensure you have:

- **Docker** installed
- **Docker Compose** installed
- **MySQL** and **Redis** (can be run via Docker)

### Step 1: Prepare VPS Environment

#### 1.1 Install Docker and Docker Compose

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt-get update
sudo apt-get install docker-compose-plugin
```

#### 1.2 Create Project Directory

```bash
mkdir -p ~/financial-tracker-api
cd ~/financial-tracker-api
```

### Step 2: Setup GitHub Secrets

For the GitHub Actions workflow to push images to DockerHub, configure these secrets in your GitHub repository:

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Add the following secrets:
   - `DOCKERHUB_USERNAME`: Your DockerHub username
   - `DOCKERHUB_TOKEN`: Your DockerHub access token

### Step 3: Configure Environment on VPS

Create a `.env` file on your VPS:

```bash
nano .env
```

Add the following configuration:

```env
SPRING_PROFILES_ACTIVE=prod

# Database Configuration
DB_HOST=mysql
DB_PORT=3306
DB_NAME=financial_tracker
DB_USER=your_db_user
DB_PASSWORD=your_secure_password

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_min_256_bits
JWT_REFRESH_SECRET=your_super_secret_refresh_key_min_256_bits
JWT_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=86400
```

### Step 4: Create Docker Compose File

Create a `docker-compose.yml` file on your VPS:

```bash
nano docker-compose.yml
```

Add the following content:

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: financial-tracker-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    networks:
      - financial-tracker-network
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    container_name: financial-tracker-redis
    ports:
      - "6379:6379"
    networks:
      - financial-tracker-network
    restart: unless-stopped

  app:
    image: YOUR_DOCKERHUB_USERNAME/financial-tracker-be:latest
    container_name: financial-tracker-app
    env_file:
      - .env
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    networks:
      - financial-tracker-network
    restart: unless-stopped

volumes:
  mysql_data:

networks:
  financial-tracker-network:
    driver: bridge
```

> **Important**: Replace `YOUR_DOCKERHUB_USERNAME` with your actual DockerHub username.

### Step 5: Deploy the Application

#### 5.1 Pull and Start Services

```bash
docker compose pull
docker compose up -d
```

#### 5.2 Check Logs

```bash
# View all logs
docker compose logs -f

# View app logs only
docker compose logs -f app
```

#### 5.3 Verify Deployment

```bash
curl http://localhost:8080
```

You should see a JSON response with API information.

### Step 6: Update Deployment (After Code Changes)

When you push changes to the `main` branch, GitHub Actions will automatically:

1. Build the application
2. Create a Docker image
3. Push it to DockerHub with the `latest` tag

To update your VPS with the new image:

```bash
cd ~/financial-tracker
docker compose pull app
docker compose up -d app
```

---

## CI/CD Workflow Explanation

The project uses GitHub Actions for automated builds and deployments.

### Workflow File

Located at: `.github/workflows/main.yml`

### Trigger

The workflow runs automatically on every push to the `main` branch.

### Steps

1. **Checkout Code**: Retrieves the latest code from the repository
2. **Setup Java 21**: Configures the Java environment
3. **Build JAR**: Compiles the application using Maven
4. **Login to DockerHub**: Authenticates with DockerHub
5. **Build & Push Image**: Creates a Docker image and pushes it to DockerHub

### Manual Trigger

You can also manually trigger the workflow from the GitHub Actions tab.

---

## Troubleshooting

### Application Won't Start

Check logs:

```bash
docker compose logs app
```

Common issues:

- Database connection failed: Verify MySQL is running and credentials are correct
- Redis connection failed: Verify Redis is running
- Port already in use: Change the port mapping in `docker-compose.yml`

### Database Migration Issues

If you need to reset the database:

```bash
docker compose down
docker volume rm financial-tracker_mysql_data
docker compose up -d
```

### Update Not Reflecting

Ensure you're pulling the latest image:

```bash
docker compose pull app
docker compose up -d app --force-recreate
```

---

## Security Recommendations

1. **Use Strong Secrets**: Generate secure random strings for JWT secrets
2. **Firewall Configuration**: Only expose necessary ports (8080, 22)
3. **HTTPS**: Use a reverse proxy (Nginx) with SSL/TLS certificates
4. **Environment Variables**: Never commit `.env` files to version control
5. **Database Backups**: Set up regular automated backups

---

## Additional Resources

- **[Setup & Installation Guide](SETUP_GUIDE.md)**: Detailed local development setup
- **[API Documentation](API_DOCS.md)**: Complete API reference
- **[Demo Scenarios](DEMO_SCENARIOS.md)**: Step-by-step usage examples
