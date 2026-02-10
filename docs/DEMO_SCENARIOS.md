# Financial Tracker Demo Scenarios

This document provides a step-by-step guide for demonstrating the Financial Tracker application using the live demo URL:
**[https://api-fintracker.suissetiawan.my.id/](https://api-fintracker.suissetiawan.my.id/)**

> ⚠️ **Note**: For these examples, we use `curl`. You can potential also use Postman or any other API client.

---

## Scenario 0: API Discovery

**Goal**: Check if the API is up and running.

```bash
curl -X GET https://api-fintracker.suissetiawan.my.id/
```

**Expected Result**: A JSON welcome message.

---

## Scenario 1: User Registration & Login

**Goal**: Create a new user and get an access token.

### 1. Register

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo_user",
    "email": "demo@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

### 2. Login

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo_user",
    "password": "password123"
  }'
```

**Action**: Copy the `accessToken` from the response. You will need it for the next steps.

```bash
export TOKEN="YOUR_ACCESS_TOKEN_HERE"
```

---

## Scenario 2: Managing Finance (User Journey)

**Goal**: Add income, expense, and check the balance.

### 1. Add Income (Salary)

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000000,
    "description": "Monthly Salary",
    "category": "Salary",
    "type": "INCOME"
  }'
```

### 2. Add Expense (Lunch)

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "description": "Lunch at Warteg",
    "category": "Food",
    "type": "EXPENSE"
  }'
```

### 3. Check Summary

```bash
curl -X GET https://api-fintracker.suissetiawan.my.id/api/summary \
  -H "Authorization: Bearer $TOKEN"
```

**Result**: Should show **Total Income**: 5,000,000 | **Total Expense**: 50,000 | **Balance**: 4,950,000.

---

## Scenario 3: Admin Features (Optional)

**Goal**: Demonstrate Admin-only features like managing categories.

### 1. Admin Login (Default Admin)

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "adminPassword"
  }'
```

**Action**: Copy the Admin Token.

```bash
export ADMIN_TOKEN="YOUR_ADMIN_TOKEN_HERE"
```

### 2. Create New Category

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/api/categories \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Investment",
    "type": "EXPENSE",
    "description": "Stocks and Bonds"
  }'
```

### 3. View All Categories (Cached)

```bash
curl -X GET https://api-fintracker.suissetiawan.my.id/api/categories \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## Scenario 4: Error Handling

**Goal**: Verify security and validation.

### 1. Unauthorized Access

Try to access summary without a token:

```bash
curl -X GET https://api-fintracker.suissetiawan.my.id/api/summary
```

**Result**: `401 Unauthorized`

### 2. Wrong Password

```bash
curl -X POST https://api-fintracker.suissetiawan.my.id/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo_user",
    "password": "wrongpassword"
  }'
```

**Result**: `401 Unauthorized` or `400 Bad Request`.
