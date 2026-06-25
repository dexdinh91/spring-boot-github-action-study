# Implementation Plan: Get user by phone

**Branch**: `001-get-user-by-phone` | **Date**: 2026-06-25 | **Spec**: [specs/001-get-user-by-phone/spec.md](spec.md)

**Input**: Feature specification from `/specs/001-get-user-by-phone/spec.md`

## Summary

Extend the Spring Boot user management API with a phone-based lookup endpoint. Implement `GET /users/phone={phone}` to retrieve a single User domain object by phone number with proper validation and error handling. Use existing repository pattern to query by phone, ensuring consistency with project architecture.

## Technical Context

**Language/Version**: Java 17 (LTS)

**Primary Dependencies**: Spring Boot 3.5.x, Spring Data JPA, Spring Web MVC

**Storage**: Existing database (H2 for tests, PostgreSQL production) accessed via Spring Data JPA

**Testing**: JUnit 5, Mockito, Spring Boot Test (@WebMvcTest, @DataJpaTest)

**Target Platform**: Linux server (web service)

**Project Type**: Web service (REST API)

**Performance Goals**: <200ms p99 response time under normal load

**Constraints**: 
- Response within 200ms (p99) per constitution
- No N+1 queries
- Proper pagination/limits
- Endpoint response follows standardized envelope format

**Scale/Scope**: Single endpoint addition; reuses existing user domain and infrastructure

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Code Quality & Maintainability**:
- ✅ Use repository pattern (Spring Data JPA) for data access
- ✅ Keep business logic in service layer
- ✅ Separate controller concerns from service logic
- ✅ Methods must not exceed 30 lines; complex validation in service
- ✅ Use @RestController annotations appropriately
- ✅ Avoid duplicating phone normalization logic; extract to utility if needed
- ✅ Cyclomatic complexity must not exceed 10 per method

**Testing Standards**:
- ✅ Minimum 85% code coverage per module
- ✅ Unit tests (<100ms): test phone normalization, null handling, lookup logic
- ✅ Integration tests (<500ms): test endpoint with @WebMvcTest
- ✅ Test naming: `testUserLookupByValidPhoneReturnsUser()`, `testUserLookupByNonExistentPhoneReturnsNotFound()`
- ✅ Use @DataJpaTest for repository layer testing

**User Experience Consistency & API Design**:
- ✅ Endpoint follows RESTful conventions: GET /users/phone={phone}
- ✅ Response must use standardized envelope format (status, code, message, data, timestamp)
- ✅ Error responses must include clear, actionable error messages (not stack traces)
- ✅ Use appropriate HTTP status codes (200, 400, 404, 500)
- ✅ Validation errors must provide field-level feedback
- ✅ All endpoints must include request/response logging (structured JSON)

**Performance Requirements**:
- ✅ Endpoint response <200ms (p99) under normal load
- ✅ Database query <50ms (indexed phone lookup)
- ✅ Ensure proper indexing on phone column
- ✅ No full table scans; use query hints if needed
- ✅ Pagination not needed (single user result)

**Status**: All gates PASS. No violations require justification.

## Project Structure

### Documentation (this feature)

```text
specs/001-get-user-by-phone/
├── spec.md              # Feature specification (input)
├── plan.md              # This file (Phase 1 output)
├── research.md          # Phase 0 output (if needed)
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── user-phone-endpoint.md
└── tasks.md             # Phase 2 output (via /speckit.tasks)
```

### Source Code (repository root)

```text
src/main/java/[package]/
├── controller/
│   ├── UserController.java              # Add GET /users/phone={phone}
├── service/
│   ├── UserService.java                 # Add findByPhone(String phone) method
│   └── PhoneNormalizer.java             # Phone normalization utility
├── repository/
│   ├── UserRepository.java              # Add custom query method
└── domain/
    └── User.java                         # Existing domain object

src/test/java/[package]/
├── controller/
│   └── UserControllerPhoneTest.java     # Integration tests for phone endpoint
├── service/
│   └── UserServicePhoneTest.java        # Service layer unit tests
├── repository/
│   └── UserRepositoryPhoneTest.java     # Repository unit tests (@DataJpaTest)
└── util/
    └── PhoneNormalizerTest.java         # Unit tests for normalization
```

**Structure Decision**: 
This is a single Spring Boot project using layered architecture (controller → service → repository → domain). The phone lookup feature follows the existing pattern:
1. **Controller layer**: Handles HTTP mapping and request/response
2. **Service layer**: Business logic (normalization, validation)
3. **Repository layer**: Spring Data JPA query
4. **Domain layer**: User entity with phone field

No new projects or modules needed; enhancement is additive within existing layers.

## Complexity Tracking

> No constitution violations; this section is not needed.

