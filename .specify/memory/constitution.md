<!-- SYNC IMPACT REPORT
=== Constitution Version Update ===
Previous Version: Not initialized (template)
New Version: 1.0.0
Bump Rationale: Initial constitution ratification for Spring Boot GitHub Action Study project with four core principles covering Code Quality, Testing Standards, User Experience Consistency, and Performance Requirements.

=== Principles Status ===
✅ Code Quality (NEW)
✅ Testing Standards (NEW)
✅ User Experience Consistency (NEW)
✅ Performance Requirements (NEW)

=== Sections Added ===
✅ Core Principles section with 4 principles
✅ Development Workflow & Quality Gates section
✅ Spring Boot Technology Stack section
✅ Governance section

=== Templates Requiring Review ===
⚠ .specify/templates/plan-template.md - Verify "Constitution Check" aligns with principles
⚠ .specify/templates/spec-template.md - Verify scope constraints reflect Code Quality & UX principles
⚠ .specify/templates/tasks-template.md - Verify task types reflect testing & performance principles
✅ .specify/templates/checklist-template.md - Ready to use
-->

# Spring Boot GitHub Action Study Constitution

A foundational document establishing the core principles, standards, and governance for the Spring Boot GitHub Action Study project. This constitution ensures code quality, robust testing practices, consistent user experience, and performant applications throughout the development lifecycle.

## Core Principles

### I. Code Quality & Maintainability

Every line of code written in this project MUST prioritize long-term maintainability, readability, and adherence to Spring Boot best practices. Code quality is non-negotiable and is validated in all code reviews.

**Standards:**
- Consistent naming conventions: CamelCase for classes/methods, camelCase for variables, UPPER_SNAKE_CASE for constants
- Methods MUST not exceed 30 lines; complex logic MUST be decomposed into smaller, testable units
- All public APIs MUST be documented with JavaDoc including parameter descriptions, return values, and thrown exceptions
- Spring annotations MUST be used appropriately (e.g., `@Component`, `@Service`, `@Repository`) to leverage dependency injection
- Code MUST follow the DRY (Don't Repeat Yourself) principle; duplicated logic across controllers, services, or repositories MUST be extracted into shared utilities
- Refactoring MUST occur after every green test pass; technical debt MUST be tracked and resolved within the sprint
- Cyclomatic complexity MUST not exceed 10 per method; methods exceeding this threshold MUST be refactored

**Rationale:** Maintainability directly impacts velocity and bug rates. Clear, readable code reduces onboarding time, enables faster debugging, and prevents subtle regressions. Spring Boot projects benefit from consistent architectural patterns (MVC, layered architecture) that this principle enforces.

### II. Testing Standards & Quality Assurance

Every feature MUST be test-driven; TDD (Test-Driven Development) is mandatory. No code ships without test coverage validating its behavior and integration.

**Standards:**
- Unit tests MUST achieve minimum 85% code coverage per module
- Integration tests MUST cover all service-layer methods and controller endpoints
- Test naming MUST follow: `test<MethodName><Scenario><ExpectedOutcome>()` (e.g., `testUserAuthenticationWithValidCredentialsReturnsToken()`)
- Unit tests MUST execute in <100ms per test; integration tests <500ms per test class
- All database-dependent tests MUST use embedded databases (H2) or testcontainers; no production database access
- Mock external dependencies (HTTP clients, message queues) using libraries like Mockito or WireMock
- Contract tests MUST validate API contracts between services (request/response schemas)
- GitHub Actions CI pipeline MUST run all tests on every push; PRs MUST not be merged without passing tests

**Rationale:** Test coverage prevents regressions and enables confident refactoring. Speed ensures developers get rapid feedback. Integration tests validate that Spring component integration (database, REST clients) works correctly. GitHub Actions automation ensures consistent quality across all commits.

### III. User Experience Consistency & API Design

All user-facing APIs and error messages MUST be consistent, predictable, and well-documented. REST endpoints, error responses, and logging MUST follow established patterns.

**Standards:**
- REST API MUST follow RESTful conventions: GET for retrieval, POST for creation, PUT/PATCH for updates, DELETE for removal
- All API responses MUST follow a standardized envelope format:
  ```json
  {
    "status": "success|error",
    "code": "HTTP_STATUS_CODE",
    "message": "Human-readable message",
    "data": { /* response body */ },
    "timestamp": "2025-01-15T10:30:00Z"
  }
  ```
- Error responses MUST include clear, actionable error messages (not stack traces) with error codes (e.g., `INVALID_INPUT`, `RESOURCE_NOT_FOUND`)
- All REST endpoints MUST return appropriate HTTP status codes (200, 201, 400, 404, 500, etc.)
- API documentation MUST be generated using Swagger/SpringDoc OpenAPI (`@RestController` classes auto-documented)
- Validation errors MUST provide field-level feedback (e.g., which field failed validation and why)
- All endpoints MUST include request/response logging (structured JSON logs with request ID for traceability)
- Breaking API changes MUST trigger major version bump; backwards-compatible changes trigger minor version bump

**Rationale:** Consistency reduces client integration friction and minimizes support overhead. Standardized error handling enables client-side error recovery. Swagger auto-documentation keeps API contracts in sync with implementation. GitHub Actions can validate API contracts in CI.

### IV. Performance Requirements & Optimization

All code MUST be optimized for performance and scalability from inception. Performance regressions MUST be prevented through monitoring and load testing.

**Standards:**
- REST endpoints MUST respond in <200ms (p99) under normal load; database queries MUST complete in <50ms
- All database queries MUST use appropriate indexes; full table scans MUST be eliminated
- N+1 query problems MUST be eliminated through eager loading (`@Query(fetch = FetchType.EAGER)`) or entity graphs
- Memory usage MUST not exceed 512MB for the application on startup; heap size MUST be tunable via environment variables
- Thread pools MUST be configured for Tomcat (default 200 threads) based on expected concurrent load
- All external API calls MUST have timeouts (default 5s); long-running operations MUST support async processing
- Response payloads MUST be paginated (default 20 items, max 100) to prevent memory exhaustion
- Caching MUST be used for frequently accessed, rarely-changed data (e.g., configuration, reference lists)
- GitHub Actions workflows MUST run performance benchmarks on every release candidate; results MUST be tracked over time

**Rationale:** Performance affects user satisfaction and operational costs. Spring Boot applications benefit from early optimization. GitHub Actions enables continuous performance monitoring. Scalability requirements prevent surprises during load spikes.

## Development Workflow & Quality Gates

All code changes MUST follow this workflow:

1. **Feature Branch**: Create branch from `main` with descriptive name (e.g., `feature/user-authentication`)
2. **Test-First Development**: Write failing tests first; implement feature; ensure tests pass
3. **Local Validation**: Run `mvn clean verify` locally before pushing; ensure no warnings
4. **Pull Request**: Open PR with detailed description linking to related issues/requirements
5. **Code Review**: At least one peer review required; all review comments MUST be addressed
6. **CI Pipeline**: GitHub Actions runs tests, linting, code coverage, and performance benchmarks
7. **Quality Gates**: PR MUST pass all checks before merge:
   - ✅ All tests passing (unit + integration)
   - ✅ Code coverage ≥85% for new code
   - ✅ No security vulnerabilities (static analysis)
   - ✅ API documentation updated if endpoints changed
8. **Merge to Main**: Branch squash-merged to `main` with descriptive commit message
9. **Release**: Tag release on `main` (semantic versioning); GitHub Actions publishes artifacts

## Spring Boot Technology Stack

This project standardizes on the following technology choices to ensure consistency:

- **Language**: Java 17 (LTS)
- **Framework**: Spring Boot 3.5.x
- **Build Tool**: Maven (via `mvn` wrapper)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **REST**: Spring Web MVC with SpringDoc OpenAPI (Swagger)
- **Data Access**: Spring Data JPA with H2 (test), PostgreSQL (production-ready)
- **Logging**: SLF4J with Logback (structured JSON logging)
- **CI/CD**: GitHub Actions
- **Static Analysis**: SonarQube (optional), CheckStyle, SpotBugs

All dependencies MUST be managed through `pom.xml` using Spring Boot parent versioning. No manual jar downloads.

## Governance

### Amendment Procedure

This constitution is binding and supersedes all informal practices. Changes MUST follow this process:

1. **Proposal**: Document amendment with rationale (why change is needed)
2. **Review**: Present to team for feedback; address concerns
3. **Approval**: Unanimously approved by core contributors
4. **Documentation**: Update constitution.md with detailed amendment explanation
5. **Version Bump**: Increment version according to semantic versioning:
   - **MAJOR**: Principle removal or redefinition (backwards-incompatible)
   - **MINOR**: New principle or materially expanded guidance
   - **PATCH**: Clarifications, wording, non-semantic refinements
6. **Migration**: If breaking change, provide migration guide and timeline for compliance

### Compliance Review

- **Code Review**: Every PR MUST be reviewed for constitution compliance (checklist)
- **Quarterly Review**: Constitution MUST be reviewed each quarter for relevance
- **Issue Tracking**: Violations MUST be tracked as technical debt; repeated violations trigger discussion

### Runtime Guidance

For day-to-day development guidance, refer to:
- `README.md` - Quick start and project overview
- `.specify/templates/` - Plan, specification, and task templates
- This constitution - Binding principles and standards

---

**Version**: 1.0.0 | **Ratified**: 2025-01-15 | **Last Amended**: 2025-01-15
