# Data Model: Get user by phone

**Feature**: Get user by phone lookup  
**Created**: 2026-06-25  
**Scope**: User domain and phone lookup patterns  

## Entities

### User (Existing Domain - Extended)

Represents an application user. This feature extends the existing User entity by adding phone-based lookup capability.

**Fields**:

| Field | Type | Constraints | Notes |
|-------|------|-----------|-------|
| `id` | UUID / Long | Primary Key, Auto-generated | Unique identifier |
| `fullName` | String | Not null, max 255 chars | User's full name |
| `phone` | String | Not null, Unique, Indexed | International format (e.g., +1234567890); stored normalized |
| `email` | String | Not null, Unique | Email address |
| `status` | Enum (ACTIVE, INACTIVE, SUSPENDED) | Not null, Default: ACTIVE | User account status |
| `createdAt` | Timestamp | Not null, Auto-set | Record creation timestamp |
| `updatedAt` | Timestamp | Not null, Auto-update | Record modification timestamp |

**Constraints & Rules**:

- **Uniqueness**: Phone is UNIQUE per user (business rule clarified in spec)
- **Normalization**: Phone values are normalized before storage and before lookup queries
  - Remove spaces, dashes, parentheses: `( ) - .` removed
  - Keep leading `+` if present
  - Example: `+1 (234) 567-8901` → `+12345678901`
- **Validation**: Phone must match pattern for international format (loose: 7-15 digits with optional leading +)
- **Index**: Database index on `phone` column (UNIQUE constraint provides natural index)
- **Sensitive fields**: `phone` and `email` are treated as sensitive; not exposed in all response contexts (e.g., user lists)

### Phone Normalization Utility

Internal utility (not a domain entity) to ensure consistent phone normalization.

**Behavior**:
- Input: raw phone string (user-provided)
- Output: normalized phone string (stored/queried format)
- Rules:
  - Strip leading/trailing whitespace
  - Remove spaces, dashes, parentheses, dots
  - Keep leading `+` if present, otherwise prepend `+` (OR leave as-is based on validation logic)
  - Lowercase special prefix chars if present

**Example transformations**:
- `+1 (234) 567-8901` → `+12345678901`
- `234-567-8901` → `2345678901` (or `+12345678901` with prefix logic)
- `+33 1 42 68 53 00` → `+33142685300`
- ` +1234567890 ` → `+1234567890`

## Relationships

**User ↔ Phone Lookup**:
- One User has exactly one phone number
- Phone uniquely identifies a User
- No junction tables needed; phone is a direct field on User

## State Transitions

User status is independent of phone lookup; phone is immutable once set (except via user edit operations handled elsewhere).

**Phone field lifecycle**:
- **Created**: Set during user account creation (required field)
- **Updated**: Can be changed via account settings (requires validation & normalization)
- **Deleted**: Removed only if user account is deleted (cascading delete)

## Validation Rules

**Phone field validation** (applied in Controller and Service):

| Scenario | Rule | Error Code |
|----------|------|-----------|
| Null/empty | Phone is required | `PHONE_REQUIRED` |
| Format | Must match international pattern (7-15 digits, optional +) | `INVALID_PHONE_FORMAT` |
| Uniqueness | Phone must not already exist in database | `PHONE_ALREADY_EXISTS` |
| Normalization | All phones normalized before storage/query | (Internal, no user error) |

## API Request/Response Contracts

### Request

```json
GET /users/phone/{phone}

Query parameters:
- phone (required, string): Phone number in any format (will be normalized)
  Example: /users/phone/1234567890 or /users/phone/234-567-8901
```

### Response (Success - 200)

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

**Notes**:
- Phone is returned normalized
- Sensitive fields (phone) are included; client has authorization responsibility
- All timestamps in ISO 8601 UTC format

### Response (Not Found - 404)

```json
{
  "status": "error",
  "code": 404,
  "message": "User with phone +12345678901 not found",
  "data": null,
  "timestamp": "2026-06-25T14:26:59Z"
}
```

### Response (Validation Error - 400)

```json
{
  "status": "error",
  "code": 400,
  "message": "Validation failed",
  "data": {
    "errors": [
      {
        "field": "phone",
        "message": "Invalid phone format. Expected international format (7-15 digits, optional + prefix)",
        "code": "INVALID_PHONE_FORMAT"
      }
    ]
  },
  "timestamp": "2026-06-25T14:26:59Z"
}
```

### Response (Server Error - 500)

```json
{
  "status": "error",
  "code": 500,
  "message": "Internal server error",
  "data": null,
  "timestamp": "2026-06-25T14:26:59Z"
}
```

## Implementation Notes

- **Database**:
  - Ensure `phone` column is indexed and has UNIQUE constraint
  - For PostgreSQL: `CREATE UNIQUE INDEX idx_user_phone ON users(phone);`
  - For H2 (test): Index handled by UNIQUE constraint

- **Spring Data JPA**:
  - Add method to UserRepository: `Optional<User> findByPhone(String normalizedPhone);`
  - Use `@Query` annotation if custom query logic needed (e.g., case-insensitive, normalization)

- **Normalization**:
  - Create utility class `PhoneNormalizer` with static method `normalize(String phone): String`
  - Call during validation in controller and before repository query in service

- **Testing**:
  - Mock database queries for unit tests
  - Use @DataJpaTest for repository layer integration tests
  - Use @WebMvcTest for controller tests
