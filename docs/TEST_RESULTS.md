# Test Results Summary

The Financial Tracker project maintains high test coverage to ensure reliability across core business logic and security components.

## Coverage Overview

| Component Group   | Coverage  | Items Tested                                                                                 |
| ----------------- | --------- | -------------------------------------------------------------------------------------------- |
| **Controllers**   | 100%      | AuthController, TransactionController, CategoryController, SummaryController, UserController |
| **Services**      | 100%      | AuthService, TransactionService, CategoryService, SummaryService, UserService                |
| **Utilities**     | 98.2%     | JwtUtils, SecurityUtils, JwtAuthFilter                                                       |
| **Exceptions**    | 100%      | GlobalExceptionHandler, Custom Handlers, Custom Exceptions                                   |
| **Total Success** | **95/95** | All tests (Unit + Integration)                                                               |

## Test Types

### Unit Tests

- Focused on individual components (Services, Controllers, Utilities).
- Uses **Mockito** for dependency mocking.
- Verifies business logic, status codes, and JSON structures.

### Integration Tests

- Verifies end-to-end flows in a real Spring context.
- Covers Authentication (JWT), Transaction management, and Categories.
- Uses an **H2 in-memory database** for isolated testing environment.

## Coverage Reporting

- **JaCoCo** is integrated into the Maven build process.
- Reports are generated after every test run and can be found at: `target/site/jacoco/index.html`
