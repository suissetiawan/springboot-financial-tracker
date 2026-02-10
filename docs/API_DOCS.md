# API Documentation

## Endpoints

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
