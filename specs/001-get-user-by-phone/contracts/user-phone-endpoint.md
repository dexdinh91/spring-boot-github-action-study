# REST API Contract: Get User by Phone

**Endpoint**: `GET /users/phone/{phone}`  
**Feature**: Retrieve a user by phone number lookup  
**API Version**: v1 (stable)  
**Status**: New endpoint (Spring Boot GET)  

## Endpoint Definition

### HTTP Method & Path

```
GET /users/phone/{phone}
```

### Path/Query Parameters

| Parameter | Type | Required | Format | Description |
|-----------|------|----------|--------|-------------|
| `phone` | String | Yes | Query param or path | Phone number in any format (spaces, dashes, parentheses allowed; will be normalized) |

**Example requests**:
- `GET /users/phone/+12345678901`
- `GET /users/phone/+1%20(234)%20567-8901` (URL-encoded: `+1 (234) 567-8901`)
- `GET /users/phone/234-567-8901`

### Request Headers

| Header | Required | Default | Description |
|--------|----------|---------|-------------|
| `Content-Type` | No | - | Client may omit (GET request, no body) |
| `Authorization` | No | - | If applicable per project auth strategy |

### Request Body

None (GET request).

## Response Contracts

### Success Response (200 OK)

**Status Code**: `200`

**Body** (JSON):

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

**Schema**:

```typescript
{
  status: "success",
  code: 200,
  message: string,
  data: {
    id: number | string (UUID),
    fullName: string,
    phone: string,              // Normalized format
    email: string,
    status: "ACTIVE" | "INACTIVE" | "SUSPENDED",
    createdAt: ISO8601 timestamp,
    updatedAt: ISO8601 timestamp
  },
  timestamp: ISO8601 timestamp
}
```

**Content-Type**: `application/json`

---

### Not Found Response (404)

**Status Code**: `404`

**Body** (JSON):

```json
{
  "status": "error",
  "code": 404,
  "message": "User with phone +12345678901 not found",
  "data": null,
  "timestamp": "2026-06-25T14:26:59Z"
}
```

**Condition**: Triggered when no user exists with the provided (normalized) phone number.

---

### Validation Error Response (400)

**Status Code**: `400`

**Body** (JSON):

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

**Conditions**:
- Phone is null, empty, or whitespace-only
- Phone format is invalid (does not match international pattern)

---

### Server Error Response (500)

**Status Code**: `500`

**Body** (JSON):

```json
{
  "status": "error",
  "code": 500,
  "message": "Internal server error",
  "data": null,
  "timestamp": "2026-06-25T14:26:59Z"
}
```

**Condition**: Unhandled exception during request processing. Response does not include stack trace (security/UX).

---

## Versioning & Compatibility

- **API Stability**: Stable for v1
- **Breaking Changes**: None
- **Backwards Compatibility**: New endpoint; no existing consumers affected
- **Future Changes**: Any change to response structure (adding/removing fields) requires major version bump (v2)

## Compliance Notes

- **Constitution**: Follows standardized response envelope format (status, code, message, data, timestamp)
- **HTTP Standards**: Uses appropriate status codes (200, 400, 404, 500)
- **Performance**: Target <200ms p99 response time (per constitution)
- **Logging**: Endpoint includes structured JSON request/response logging with request ID

## Examples

### Example 1: Successful Lookup

**Request**:
```
GET /users/phone/+12345678901
```

**Response** (200):
```json
{
  "status": "success",
  "code": 200,
  "message": "User found",
  "data": {
    "id": 42,
    "fullName": "Alice Smith",
    "phone": "+12345678901",
    "email": "alice@example.com",
    "status": "ACTIVE",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-06-20T14:15:30Z"
  },
  "timestamp": "2026-06-25T14:26:59Z"
}
```

### Example 2: Phone Not Found

**Request**:
```
GET /users/phone/+19999999999
```

**Response** (404):
```json
{
  "status": "error",
  "code": 404,
  "message": "User with phone +19999999999 not found",
  "data": null,
  "timestamp": "2026-06-25T14:26:59Z"
}
```

### Example 3: Invalid Phone Format

**Request**:
```
GET /users/phone/not-a-phone
```

**Response** (400):
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

