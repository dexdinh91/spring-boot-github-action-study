# Tasks: Get User by Phone

**Input**: Design documents from `/specs/001-get-user-by-phone/`

**Prerequisites**: plan.md (Spring Boot 3.5.x, Java 17), spec.md (3 user stories), data-model.md, contracts/user-phone-endpoint.md

**Tests**: Included for TDD approach per constitution testing standards (minimum 85% coverage required)

**Organization**: Tasks grouped by user story priority (P1, P2, P3) to enable independent implementation and testing

**Architecture**: Spring Boot layered pattern (controller → service → repository → domain)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and Spring Boot structure

- [X] T001 Ensure Spring Boot 3.5.x project structure exists with `src/main/java`, `src/test/java`, and `pom.xml`
- [ ] T002 [P] Verify Maven dependencies in `pom.xml` include: Spring Boot, Spring Data JPA, JUnit 5, Mockito
- [ ] T003 [P] Configure application.properties or application.yml for database configuration (H2 for tests, PostgreSQL for production)
- [ ] T004 [P] Verify linting and code style tools configured (CheckStyle, SpotBugs in Maven build)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and shared utilities that MUST complete before user story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Create `PhoneNormalizer` utility class in `src/main/java/[package]/util/PhoneNormalizer.java` with:
  - Static method `normalize(String phone): String`
  - Logic: strip spaces, dashes, parentheses, dots; keep leading +
  - Handle null/empty input gracefully
- [ ] T006 Add phone uniqueness constraint to User entity in `src/main/java/[package]/domain/User.java`:
  - Add `@Column(unique = true)` annotation to phone field
  - Ensure phone field is NOT null
  - Add index: `@Index(name = "idx_user_phone", columnList = "phone")`
- [X] T007 [P] Create custom UserRepository method in `src/main/java/[package]/repository/UserRepository.java`:
  - Add `Optional<User> findByPhone(String normalizedPhone);`
  - This enables repository-level phone lookup
- [X] T008 Implement standardized error handling and response envelope in `src/main/java/[package]/api/ApiResponse.java`:
  - Implement response wrapper with fields: status, code, message, data, timestamp
  - Create `@RestControllerAdvice` for global exception handling in `src/main/java/[package]/api/GlobalExceptionHandler.java`
  - Handle validation errors, not-found errors, and server errors with proper HTTP status codes
- [X] T009 [P] Add request/response logging configuration in `src/main/resources/application.properties`:
  - Configure SLF4J with structured JSON logging
  - Ensure each request has unique request ID for traceability

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Retrieve User by Phone (Priority: P1) 🎯 MVP

**Goal**: Implement `GET /users/phone={phone}` endpoint to retrieve a user by phone number with proper validation and success handling

**Independent Test**: 
```bash
curl -X GET "http://localhost:8080/users/phone=%2B12345678901"
# Expected: 200 OK with User domain in standardized response envelope
```

### Tests for User Story 1 (TDD - Write First, Ensure FAIL before Implementation)

- [ ] T010 [P] [US1] Create contract test in `src/test/java/[package]/api/UserPhoneEndpointContractTest.java`:
  - Test successful phone lookup (200 response, correct User data)
  - Test response envelope structure (status, code, message, data, timestamp all present)
  - Verify phone is normalized in response
- [ ] T011 [P] [US1] Create repository unit test in `src/test/java/[package]/repository/UserRepositoryPhoneTest.java`:
  - Test `findByPhone()` returns correct user for matching phone
  - Test `findByPhone()` returns empty Optional for non-existent phone
  - Test with various phone formats (ensure normalization happens before query)
- [ ] T012 [P] [US1] Create service unit test in `src/test/java/[package]/service/UserServicePhoneTest.java`:
  - Test `findByPhone()` calls repository and returns User
  - Test `findByPhone()` throws ResourceNotFoundException when phone not found
  - Test phone normalization happens before lookup
- [ ] T013 [P] [US1] Create controller integration test in `src/test/java/[package]/api/UserControllerPhoneIT.java` using @WebMvcTest:
  - Test endpoint returns 200 with User data for valid phone
  - Test endpoint returns standardized response format
  - Test performance: response time <200ms

### Implementation for User Story 1

- [ ] T014 [US1] Update User entity in `src/main/java/[package]/domain/User.java`:
  - Verify phone field exists with proper annotations (@Column(unique=true), @Index, @NotNull)
  - No additional changes needed (domain already supports phone via foundation)
- [ ] T015 [US1] Create UserService method in `src/main/java/[package]/service/UserService.java`:
  - Add method: `public User findByPhone(String phone) throws ResourceNotFoundException`
  - Implementation: normalize phone using `PhoneNormalizer.normalize()`, call repository, throw exception if not found
  - Keep method under 20 lines
- [ ] T016 [US1] Create UserController endpoint in `src/main/java/[package]/api/UserController.java`:
  - Add method: `@GetMapping("/users/phone={phone}")`
  - Implementation: call `userService.findByPhone()`, wrap response in ApiResponse envelope
  - Verify response status is 200 for success
- [ ] T017 [US1] Add validation to UserController endpoint:
  - Validate phone parameter is not empty
  - Return 400 with validation error if phone is empty
  - Add `@NotBlank` validation on phone parameter
- [ ] T018 [US1] Add structured logging in UserService and UserController:
  - Log incoming phone lookup request (with request ID)
  - Log successful User found (with user ID, no sensitive data)
  - Log phone not found error (with phone, not user details)
  - Use structured JSON format per constitution

**Checkpoint**: User Story 1 complete. Test independently: `mvn verify` should pass with ≥85% coverage for US1 code.

---

## Phase 4: User Story 2 - Handle Not Found (Priority: P2)

**Goal**: Ensure endpoint properly handles cases where no user exists with requested phone

**Independent Test**:
```bash
curl -X GET "http://localhost:8080/users/phone=%2B19999999999"
# Expected: 404 Not Found with message "User with phone +19999999999 not found"
```

### Tests for User Story 2 (TDD - Write First, Ensure FAIL before Implementation)

- [ ] T019 [P] [US2] Create contract test for not-found case in `src/test/java/[package]/api/UserPhoneEndpointContractTest.java`:
  - Test 404 response for non-existent phone
  - Verify error response has status=error, code=404, message with phone number
  - Verify data field is null
- [ ] T020 [P] [US2] Create integration test for not-found scenario in `src/test/java/[package]/api/UserControllerPhoneIT.java`:
  - Test endpoint returns 404 for phone that doesn't exist in database
  - Test error message includes phone number and is actionable
  - Test response time is <200ms

### Implementation for User Story 2

- [ ] T021 [US2] Create ResourceNotFoundException in `src/main/java/[package]/exception/ResourceNotFoundException.java`:
  - Extend RuntimeException
  - Constructor: `ResourceNotFoundException(String message)`
  - Used when phone lookup returns empty
- [ ] T022 [US2] Update UserService in `src/main/java/[package]/service/UserService.java`:
  - Ensure `findByPhone()` throws `ResourceNotFoundException` when Optional is empty
  - Exception message: `"User with phone [normalizedPhone] not found"`
- [ ] T023 [US2] Update GlobalExceptionHandler in `src/main/java/[package]/api/GlobalExceptionHandler.java`:
  - Add handler method for `ResourceNotFoundException`
  - Return 404 status code with ApiResponse(error, 404, message, null, timestamp)
  - Do not expose stack trace in response (security/UX)
- [ ] T024 [US2] Verify UserController error handling in `src/main/java/[package]/api/UserController.java`:
  - Endpoint should NOT explicitly catch ResourceNotFoundException
  - Let GlobalExceptionHandler catch it (separation of concerns)
  - Confirm all 404 responses are consistent across project

**Checkpoint**: User Stories 1 & 2 complete. Test independently: `mvn verify` passes; 404 responses are consistent and actionable.

---

## Phase 5: User Story 3 - Validate Phone Format (Priority: P3)

**Goal**: Validate incoming phone parameter and reject malformed input with clear error message

**Independent Test**:
```bash
# Invalid format
curl -X GET "http://localhost:8080/users/phone=not-a-phone"
# Expected: 400 Bad Request with field error explaining required format

# Empty phone
curl -X GET "http://localhost:8080/users/phone="
# Expected: 400 Bad Request with message "Phone is required"
```

### Tests for User Story 3 (TDD - Write First, Ensure FAIL before Implementation)

- [ ] T025 [P] [US3] Create validation test in `src/test/java/[package]/api/UserPhoneEndpointContractTest.java`:
  - Test 400 response for invalid phone format
  - Test error message includes field name (phone) and expected format
  - Test 400 response for empty phone parameter
- [ ] T026 [P] [US3] Create unit test for PhoneNormalizer validation in `src/test/java/[package]/util/PhoneNormalizerTest.java`:
  - Test normalization works for valid international formats (7-15 digits, optional +)
  - Test invalid formats are rejected (letters, symbols other than +-)
  - Test null/empty input is handled
- [ ] T027 [P] [US3] Create integration test for validation edge cases in `src/test/java/[package]/api/UserControllerPhoneIT.java`:
  - Test endpoint returns 400 for: empty string, non-phone characters, too-short input, too-long input
  - Test response time remains <200ms even for validation errors

### Implementation for User Story 3

- [ ] T028 [US3] Add phone validation to PhoneNormalizer in `src/main/java/[package]/util/PhoneNormalizer.java`:
  - Add validation method: `isValidPhoneFormat(String phone): boolean`
  - Validation rule: 7-15 digits with optional leading + and formatting characters (spaces, dashes, parentheses, dots)
  - Examples: valid=`+12345678901`, `+1 (234) 567-8901`, `2345678901`; invalid=`abc`, `12`, `+1 a34 567`
- [ ] T029 [US3] Add validation method to UserService in `src/main/java/[package]/service/UserService.java`:
  - Add method: `private void validatePhoneInput(String phone) throws ValidationException`
  - Check: not null, not empty (after trim), not blank
  - Check: phone format valid using `PhoneNormalizer.isValidPhoneFormat()`
  - Throw `ValidationException` with specific error code and message
- [ ] T030 [US3] Create ValidationException in `src/main/java/[package]/exception/ValidationException.java`:
  - Include fields: `field` (String), `message` (String), `errorCode` (String)
  - Allows granular field-level error reporting
- [ ] T031 [US3] Update UserController endpoint in `src/main/java/[package]/api/UserController.java`:
  - Call `userService.validatePhoneInput(phone)` before `findByPhone()`
  - Do NOT catch ValidationException; let GlobalExceptionHandler handle it
- [ ] T032 [US3] Update GlobalExceptionHandler in `src/main/java/[package]/api/GlobalExceptionHandler.java`:
  - Add handler for `ValidationException`
  - Return 400 status code with ApiResponse including field-level errors
  - Example: `"data": { "errors": [{ "field": "phone", "message": "Invalid format...", "code": "INVALID_PHONE_FORMAT" }] }`
- [ ] T033 [US3] Add logging for validation errors in UserService:
  - Log validation failures with phone value (anonymized if sensitive) and error reason
  - Use appropriate log level (WARN for validation failures)

**Checkpoint**: All user stories complete. Test independently: `mvn verify` passes; validation errors are clear and field-level.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements affecting all user stories and final quality gates

- [ ] T034 [P] Ensure 85% code coverage across all modules:
  - Run: `mvn clean verify jacoco:report`
  - Generate coverage report: `target/site/jacoco/index.html`
  - Verify coverage includes: PhoneNormalizer, UserService, UserController, Repository, Exception handlers
  - Add any missing edge case tests
- [ ] T035 [P] Run quickstart.md validation scenarios:
  - Test successful lookup (Scenario A)
  - Test not found (Scenario B)
  - Test invalid format (Scenario C)
  - Test empty phone (Scenario D)
  - Test phone normalization (Scenario E)
- [ ] T036 [P] Verify performance requirements:
  - Load test: endpoint responds in <200ms p99 under normal load
  - Database query responds in <50ms (check logs or profiler)
  - Verify UNIQUE index on phone column exists in database schema
- [ ] T037 Performance optimization if needed:
  - If tests fail performance gate: profile with JProfiler or YourKit
  - Optimize queries: verify no N+1 queries, proper eager loading
  - Optimize normalization: cache if needed (unlikely for single lookup)
- [ ] T038 Documentation updates in `docs/` or `README.md`:
  - Document endpoint: `GET /users/phone={phone}`
  - Document phone format requirements (7-15 digits, optional +)
  - Document response envelope structure (from ApiResponse)
  - Reference: specs/001-get-user-by-phone/contracts/user-phone-endpoint.md
- [ ] T039 Code review checklist:
  - Verify all public methods have JavaDoc (constitution requirement)
  - Verify no method exceeds 30 lines (constitution requirement)
  - Verify methods use @Component, @Service, @Repository appropriately
  - Verify DRY principle: no phone normalization logic duplicated
  - Verify Cyclomatic complexity: no method > 10 (use static analysis tool)
- [ ] T040 [P] Security hardening:
  - Verify response does not expose: passwords, security tokens, internal system details
  - Verify error messages are user-friendly, not stack traces
  - Verify no SQL injection vectors in custom queries
  - Verify request/response logging does not expose sensitive data

**Checkpoint**: Feature complete. All tests passing, documentation updated, performance validated.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - **BLOCKS all user stories**
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May enhance US1 (both use same endpoint)
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May enhance US1 & US2 (all use same endpoint)

**Note**: In this feature, all three user stories enhance the same `GET /users/phone={phone}` endpoint. They are independent in terms of testing but share the same code. Implementation order: US1 (success path) → US2 (not-found error path) → US3 (validation error path).

### Within Each User Story

- Tests MUST be written first (TDD) and FAIL before implementation
- PhoneNormalizer → UserService → UserController (layers)
- Service before controller
- Core implementation before integration
- Story complete when all tests pass with ≥85% coverage

### Parallel Opportunities (Single Developer: Sequential)

Since this is a small feature with one endpoint, sequential is recommended:

1. **Phase 1 Setup** (4 tasks): 30 min
2. **Phase 2 Foundational** (5 tasks, 2 parallelizable): 2 hours
3. **Phase 3 User Story 1** (7 test tasks, 4 implementation tasks): 3-4 hours
4. **Phase 4 User Story 2** (2 test tasks, 4 implementation tasks): 1.5-2 hours
5. **Phase 5 User Story 3** (3 test tasks, 6 implementation tasks): 2-3 hours
6. **Phase 6 Polish** (8 tasks, 6 parallelizable): 1-2 hours

**Total Estimate**: 10-14 hours (with testing, code review, documentation)

### Parallel Opportunities (Team: Parallel Phases)

With multiple developers (less likely for this small feature):

```
Developer A (Foundation):  Phase 1 → Phase 2 → US1 implementation
Developer B (Testing):     US1 tests → US2 tests → US3 tests  
Developer C (Polish):      Wait for Phase 2 → Polish & validation

Order: A completes Phase 2 → B writes all tests → A/B implement US1/2/3 → C runs polish
```

---

## Implementation Strategy

### MVP First (User Story 1 Only) ⭐ RECOMMENDED

1. Complete Phase 1: Setup (10 min)
2. Complete Phase 2: Foundational (2 hours)
3. Complete Phase 3: User Story 1 (3-4 hours)
4. **STOP and VALIDATE**: 
   ```bash
   mvn verify
   curl -X GET "http://localhost:8080/users/phone=%2B12345678901"
   ```
5. Deploy/demo if ready (working endpoint for successful lookup)

**Result**: Working API endpoint, 85% test coverage, ready for user feedback

### Incremental Delivery

1. MVP: User Story 1 (successful lookup)
2. Error Handling: Add User Story 2 (404 not found)
3. Input Validation: Add User Story 3 (400 validation errors)
4. Polish: Add documentation, performance validation, code review

Each story builds on previous; each remains testable independently.

---

## Task Tracking & Validation

**Before Starting**:
- [ ] Phase 1 & 2 foundational work complete
- [ ] Project builds cleanly: `mvn clean compile`
- [ ] Tests can run: `mvn clean test`

**After Each Phase**:
- [ ] All tasks checked off (✅)
- [ ] All tests pass: `mvn clean verify`
- [ ] Coverage meets 85%: `mvn jacoco:report` → verify target/site/jacoco/index.html
- [ ] Code style passes: `mvn clean verify` (CheckStyle, SpotBugs run in Maven)
- [ ] No new warnings introduced

**MVP Checkpoint (after US1)**:
- Endpoint works: `curl -X GET "http://localhost:8080/users/phone=%2B12345678901"`
- Test passes: `mvn clean verify` - all US1 tests pass
- Coverage >85% for: PhoneNormalizer, UserService, UserController, UserRepository
- Ready to commit and deploy

---

## Notes

- Each task includes exact file paths for clarity
- [P] marked tasks = can run in parallel (different files, no dependencies)
- [Story] label maps task to user story for traceability (T###  [US#])
- Tests come BEFORE implementation (TDD - write failing tests first)
- Each user story independently completable and testable
- Stop at any checkpoint to validate story independently
- Reference design artifacts: plan.md, data-model.md, contracts/, quickstart.md for detailed specs
- Constitution compliance: 85% coverage, methods <30 lines, proper annotations, no duplicated logic
