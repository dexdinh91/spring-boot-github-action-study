# Feature Specification: Get user by phone

**Feature Branch**: `001-get-user-by-phone`

**Created**: 2026-06-25

**Status**: Draft

**Input**: User description: "I want to build an additional API which has format /users/phone=xxx to get the user by phone number, the output should be User domain, in the repository, you can create a dummy return"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Retrieve user by phone (Priority: P1)

An API consumer (internal service or client) requests user information by providing a phone number in the path `/users/phone=xxx`.

**Why this priority**: Primary business need is to locate a user by phone (commonly used lookup).

**Independent Test**: Call `GET /users/phone=+1234567890` and verify the response contains the User domain object for that phone number.

**Acceptance Scenarios**:

1. **Given** a user exists with phone `+1234567890`, **When** the client calls `GET /users/phone=+1234567890`, **Then** the API returns 200 with the User domain in the response.
2. **Given** multiple users with different phones, **When** a phone lookup is performed, **Then** exactly the user matching the provided phone is returned.

---

### User Story 2 - Handle not found (Priority: P2)

Client requests a phone that does not exist.

**Why this priority**: Important for correct client behavior and error handling.

**Independent Test**: Call `GET /users/phone=+0000000000` and verify a 404 (or appropriate not-found response) with a clear message.

**Acceptance Scenarios**:

1. **Given** no user exists with phone `+0000000000`, **When** the client calls the endpoint, **Then** the API responds with a not-found status and message indicating no user found for that phone.

---

### User Story 3 - Invalid phone format (Priority: P3)

Client provides a malformed phone string.

**Why this priority**: Ensures input validation and avoids ambiguous lookups.

**Independent Test**: Call `GET /users/phone=invalid-phone` and verify a 400 (bad request) with a validation error explaining the expected phone format.

**Acceptance Scenarios**:

1. **Given** the phone parameter is not a valid phone string, **When** the client calls the endpoint, **Then** the API returns a validation error explaining acceptable phone formats.

---

### Edge Cases

- Empty phone parameter (e.g., `/users/phone=`) — return 400 with validation error.
- Phone with international prefixes and formatting differences — normalization expected before lookup.
- Multiple accounts sharing the same phone (if allowed by domain) — define business rule (current assumption: phone is unique per user).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Provide an endpoint `GET /users/phone={phone}` that returns the User domain for the matching phone number.
- **FR-002**: If a user with the provided phone exists, the system MUST return a success response containing the full User domain.
- **FR-003**: If no user exists with the provided phone, the system MUST return a clear not-found response.
- **FR-004**: The system MUST validate the phone parameter and return a validation error for malformed input.
- **FR-005**: Phone lookup SHOULD be normalized (trim, remove formatting characters) before matching.
- **FR-006**: The endpoint MUST not expose sensitive fields (e.g., passwords, security tokens) in the returned User domain.

### Key Entities

- **User**: Represents an application user with attributes relevant to consumers. Key attributes (domain-level, not implementation): `id`, `fullName`, `phone`, `email`, `status`, `createdAt`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Given an existing phone number, 100% of successful lookup requests return the correct User domain (functional correctness).
- **SC-002**: For not-found cases, 100% of requests return a clear not-found result (status and message) that allows clients to handle the case.
- **SC-003**: Validation errors are returned for malformed phone inputs in 100% of such requests.
- **SC-004**: 95% of lookup requests return a response within 200ms under normal load.

## Assumptions

- Phone numbers are unique per user in the current domain.
- Phone normalization rules follow common international formatting (strip spaces, dashes, parentheses; keep leading + when present).
- Authentication/authorization is handled elsewhere; this spec focuses on the endpoint behavior and responses.
- Dummy return in repository code is acceptable for initial implementation and tests.


--

