# Deployment Guide

This guide details how to deploy the **Financial Tracker API** to a production VPS using Docker and GitHub Actions.

> 🟢 **Live Demo**: [https://api-fintracker.suissetiawan.my.id/](https://api-fintracker.suissetiawan.my.id/)

---

## 📋 Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Step 1: VPS Preparation](#step-1-vps-preparation)
3. [Step 2: GitHub Repository Setup](#step-2-github-repository-setup)
4. [Step 3: Server Configuration](#step-3-server-configuration)
5. [Step 4: Continuous Deployment (CI/CD)](#step-4-continuous-deployment-cicd)
6. [Troubleshooting](#troubleshooting)

---

## 1. Prerequisites

Before starting, ensure you have:

- A **VPS** (e.g., DigitalOcean, AWS EC2, Linode) with Linux (Ubuntu 22.04 recommended).
- A **Domain Name** pointed to your VPS IP (e.g., `api-fintracker.suissetiawan.my.id`).
- **DockerHub Account** for storing container images.
- Access to this **GitHub Repository**.

---

## Step 1: VPS Preparation

SSH into your VPS and set up the environment.

### 1.1 Install Docker & Docker Compose

```bash
# Update and install helper tools
sudo apt update && sudo apt install -y curl

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Verify installation
docker --version
docker compose version
```

### 1.2 Create Project Directory

```bash
mkdir -p ~/fintracker-api
cd ~/fintracker-api
```

### 1.3 Create Docker Network

Create a dedicated network for the application and its services to communicate:

```bash
docker network create financial-tracker-network
```

### 1.4 Install MySQL

Run the MySQL container manually. Ensure you replace the placeholders with your actual secure passwords.

```bash
# Define your variables (or replace directly in the command)
export DB_ROOT_PASSWORD=your_root_password
export DB_NAME=financial_tracker
export DB_USER=your_db_user
export DB_PASSWORD=your_db_password

# Run MySQL
docker run -d \
  --name financial-tracker-mysql \
  --network financial-tracker-network \
  -e MYSQL_ROOT_PASSWORD=$DB_ROOT_PASSWORD \
  -e MYSQL_DATABASE=$DB_NAME \
  -e MYSQL_USER=$DB_USER \
  -e MYSQL_PASSWORD=$DB_PASSWORD \
  -v mysql_data:/var/lib/mysql \
  --restart always \
  mysql:8.0
```

### 1.5 Install Redis

Run the Redis container attached to the same network:

```bash
docker run -d \
  --name financial-tracker-redis \
  --network financial-tracker-network \
  --restart always \
  redis:7-alpine
```

### 1.6 Generate SSH Keys for Deployment

To allow GitHub Actions to SSH into your VPS, generating a dedicated SSH key pair is recommended.

1.  **Generate the key pair** (run this on your local machine or VPS, but keep the private key safe):

    ```bash
    ssh-keygen -t ed25519 -C "deploy-fintracker-key" -f ./deploy_key
    ```

    This creates two files: `deploy_key` (Private) and `deploy_key.pub` (Public).

2.  **Add Public Key to VPS**:
    Copy the contents of `deploy_key.pub` and append it to `~/.ssh/authorized_keys` on your VPS.

    ```bash
    cat deploy_key.pub >> ~/.ssh/authorized_keys
    ```

3.  **Store Private Key**:
    Copy the content of `deploy_key`. You will need this for the `VPS_SSH_KEY` secret in GitHub.

---

## Step 2: GitHub Repository Setup

To enable automated deployments, configure these secrets in your GitHub repository:

1.  Go to **Settings** > **Secrets and variables** > **Actions**.
2.  Create the following **Repository Secrets**:

| Secret Name          | Description                                                                      |
| :------------------- | :------------------------------------------------------------------------------- |
| `DOCKERHUB_USERNAME` | Your DockerHub username.                                                         |
| `DOCKERHUB_TOKEN`    | Your DockerHub Access Token (Create at DockerHub > Account Settings > Security). |
| `VPS_HOST`           | Typically the IP address of your VPS.                                            |
| `VPS_USER`           | The username to SSH into (e.g., `root` or `ubuntu`).                             |
| `VPS_SSH_KEY`        | The **Private Key** content you generated in Step 1.3.                           |

---

## Step 3: Server Configuration

You only need to configure the environment variables once. The application code and Docker configuration will be deployed automatically.

### 3.1 Create Environment File

Create a `.env` file in your project directory on the VPS:

```bash
cd ~/fintracker-api
nano .env
```

Paste your production configuration:

```env
SPRING_PROFILES_ACTIVE=prod
DOCKERHUB_USERNAME=<your-dockerhub-username>

# Database
DB_HOST=<your-db-host>
DB_PORT=3306
DB_NAME=financial_tracker
DB_USER=<your-db-user>
DB_PASSWORD=<your-db-password>

# Redis
REDIS_HOST=<your-redis-host>
REDIS_PORT=6379

# Security
JWT_SECRET=YOUR_SUPER_SECURE_LONG_RANDOM_STRING_MIN_256_BITS
JWT_REFRESH_SECRET=YOUR_OTHER_SUPER_SECURE_LONG_RANDOM_STRING
JWT_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=86400

# App Port
APP_PORT=8080
```

> **Note**: You do **NOT** need to create `docker-compose.yml` manually. The CI/CD workflow will automatically copy the latest version from the repository to your server.

---

## Step 4: Continuous Deployment (CI/CD)

The project includes a **GitHub Actions** workflow that handles the entire deployment process.

### Automated Workflow

Every time you push to the `main` branch, the following happens automatically:

1.  **Build & Test**: Java code is compiled and tested.
2.  **Docker Build**: A new Docker image is built and pushed to DockerHub.
3.  **Deploy to VPS**:
    - The workflow logs into your VPS using the SSH secrets.
    - It copies the latest `docker-compose.yml` and `script/deploy.sh` (if applicable).
    - It pulls the new image and restarts the services.

### Manual Verification

After a successful deployment, the service will start automatically. You can verify the status on your VPS:

```bash
cd ~/fintracker-api
docker compose ps
docker compose logs -f app
```

---

## Troubleshooting

### Database Connection Refused

- Ensure `mysql` container is running.
- Check that `DB_HOST`, `DB_USER`, and `DB_PASSWORD` in your `.env` file are correct.

### App Crashes on Start

- Check logs: `docker compose logs app`
- Verify `JWT_SECRET` is set and valid.

### GitHub Action Fails on SSH

- Ensure `VPS_SSH_KEY` is the **private** key.
- Ensure the corresponding **public** key is in `~/.ssh/authorized_keys` on the VPS.
- Check that `VPS_USER` has permissions to write to `~/fintracker-api`.

---

> For local development instructions, see **[SETUP_GUIDE.md](SETUP_GUIDE.md)**.
