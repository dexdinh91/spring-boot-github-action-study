# Quickstart: Get User by Phone

**Feature**: Retrieve a user by phone number via REST API  
**Endpoint**: `GET /users/phone/{phone}`  
**Framework**: Spring Boot 3.5.x with Spring Web MVC  

## Prerequisites

- Java 17+ installed (`java -version` should show 17.x or higher)
- Maven installed (`mvn -version` should show Maven 3.8.x or higher)
- Git repository cloned locally
- Database accessible (H2 for testing, PostgreSQL for production)
- Spring Boot application running (port 8080 by default, or configured `server.port`)

## Quick Start: Local Testing

### 1. Build the project

```bash
cd /path/to/spring-boot-github-action-study
mvn clean install
```

Expected: Build succeeds with `BUILD SUCCESS`.

### 2. Start the Spring Boot application

```bash
mvn spring-boot:run
```

Expected: Application starts on http://localhost:8080 (or configured port).

### 3. Verify the endpoint is live

```bash
curl -s http://localhost:8080/users/phone/+12345678901 | jq .
```

Expected response (if user exists):
```json
{
  "status": "success",
  "code": 200,
  "message": "User found",
  "data": {
    "id": 1,
    "fullName": "John Doe",
    "phone": "+12345678901",
    "email": "john@example.com",
    "status": "ACTIVE",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z"
  },
  "timestamp": "2026-06-25T14:26:59Z"
}
```

### 4. Test scenarios

#### Scenario A: Successful lookup

```bash
# Test: User exists with phone +12345678901
curl -X GET "http://localhost:8080/users/phone/%2B12345678901" \
  -H "Content-Type: application/json"

# Expected: 200 OK with User data in response
```

#### Scenario B: Phone not found

```bash
# Test: No user with phone +19999999999
curl -X GET "http://localhost:8080/users/phone/%2B19999999999" \
  -H "Content-Type: application/json"

# Expected: 404 Not Found with message "User with phone +19999999999 not found"
```

#### Scenario C: Invalid phone format

```bash
# Test: Invalid phone format
curl -X GET "http://localhost:8080/users/phone/not-a-phone" \
  -H "Content-Type: application/json"

# Expected: 400 Bad Request with field error indicating invalid format
```

#### Scenario D: Empty phone parameter

```bash
# Test: Empty phone (URL-encoded empty string)
curl -X GET "http://localhost:8080/users/phone/" \
  -H "Content-Type: application/json"

# Expected: 400 Bad Request with error "Phone is required"
```

#### Scenario E: Phone normalization

```bash
# Test: Phone with formatting (spaces, dashes, parentheses)
curl -X GET "http://localhost:8080/users/phone/%2B1%20%28234%29%20567-8901" \
  -H "Content-Type: application/json"
# Decoded: +1 (234) 567-8901

# Expected: 200 OK if normalized form (+12345678901) matches a user
```

## Unit Test Execution

Run all tests:

```bash
mvn clean test
```

Expected: Tests pass with coverage report generated.

### Run phone-related tests only

```bash
mvn test -Dtest=UserControllerPhoneTest,UserServicePhoneTest,PhoneNormalizerTest
```

Expected: All phone-related tests pass (>85% coverage for affected code).

### Run integration tests (if separated)

```bash
mvn verify
```

Expected: All integration tests pass; application can be deployed.

## Database Setup (Production)

### PostgreSQL

Ensure the User table has a UNIQUE index on the phone column:

```sql
CREATE UNIQUE INDEX idx_user_phone ON users(phone);
```

Or add during table creation/migration:

```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  phone VARCHAR(20) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### H2 (Testing)

H2 automatically handles UNIQUE constraints; no additional setup needed.

## Validation Checklist

After running quickstart tests, verify:

- [ ] Endpoint responds with 200 for valid phone lookup (user exists)
- [ ] Endpoint responds with 404 for non-existent phone
- [ ] Endpoint responds with 400 for invalid phone format
- [ ] Endpoint responds with 400 for empty phone
- [ ] Phone normalization works (e.g., `+1 (234) 567-8901` matches `+12345678901` in database)
- [ ] Response envelope includes status, code, message, data, timestamp fields
- [ ] Response does not expose sensitive fields (passwords, security tokens)
- [ ] Response time is <200ms (verify with `curl -w "@curl-format.txt"` or similar)
- [ ] Structured JSON logs are produced (check application logs)
- [ ] Unit tests pass with ≥85% code coverage
- [ ] Integration tests pass without database errors

## Troubleshooting

### Issue: Port 8080 already in use

**Solution**: Change port in `application.properties` or `application.yml`:

```properties
server.port=8081
```

Then restart the application.

### Issue: Database connection error

**Solution**: Verify database is running and credentials are correct in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
```

### Issue: Endpoint returns 404

**Solution**: 
1. Verify application is running: `curl http://localhost:8080/actuator/health`
2. Verify user exists with that phone in database
3. Check application logs for errors
4. Verify phone normalization is working (check service logs)

### Issue: Tests fail with 85% coverage threshold

**Solution**: 
1. Ensure all phone-related code paths are tested (success, 404, 400, edge cases)
2. Review coverage report: `target/site/jacoco/index.html`
3. Add tests for uncovered branches (e.g., error handling in normalization)

## References

- **API Contract**: See [user-phone-endpoint.md](contracts/user-phone-endpoint.md)
- **Data Model**: See [data-model.md](data-model.md)
- **Feature Spec**: See [spec.md](spec.md)
- **Implementation Plan**: See [plan.md](plan.md)
- **Constitution**: See [.specify/memory/constitution.md](.specify/memory/constitution.md)

