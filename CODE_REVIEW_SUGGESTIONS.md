# Code Review – E-Commerce Application

Summary of findings and suggested changes after reviewing the codebase.

---

## Critical (Security & Correctness)

### 1. **Login does not validate password (user-management-service)**

**Location:** `AuthController.login()`

**Issue:** The login endpoint accepts email (and password in the body) but **never checks the password**. It generates a JWT for any given email.

```java
// Current: issues token for any email
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    String token = jwtUtil.generateToken(request.getEmail());
    return ResponseEntity.ok(new LoginResponse(token));
}
```

**Suggestion:** Authenticate using `AuthenticationManager` (and optionally `UserDetailsService`), then generate the token only when authentication succeeds.

```java
// Example: inject AuthenticationManager, then:
Authentication auth = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
String token = jwtUtil.generateToken(request.getEmail());
return ResponseEntity.ok(new LoginResponse(token));
```

Also return **401** when credentials are invalid instead of 200 with a token.

---

### 2. **User update API missing (backend)**

**Location:** Frontend `Users.js` calls `api.put(\`/api/user/${editingId}\`, formData)` but **UserController has no PUT endpoint** and **UserService has no update method**.

**Suggestion:**

- Add `UserService.updateUser(Long id, UserRequest request)` (and implementation in `UserServiceImpl`).
- In `UserController` add:

```java
@PutMapping("/{id}")
public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
    return ResponseEntity.ok(userService.updateUser(id, request));
}
```

- In `UserServiceImpl`: load user by id, update name/email/password (encode password if present), avoid changing email to an already-used one (except for same user), save and return mapped response.

---

### 3. **Passwords returned in API responses**

**Location:** `UserResponse` includes `password`; `UserServiceImpl.mapToResponse()` sets decrypted password on the response.

**Issue:** Passwords (even decrypted) must not be sent to the client. This is a security and compliance risk.

**Suggestion:**

- Remove `password` from `UserResponse` (or use a separate DTO for admin/internal use that never includes password).
- Do not set or expose decrypted password in any API response. If the frontend needs to show a placeholder for “change password”, use a sentinel like `"********"` or omit the field.

---

### 4. **JWT secret and encryption keys in config**

**Location:** `config_properties/application.yml` has `application.secret.key` in plain text; `user-management-service.yml` has default `encryption.secret-key`.

**Issue:** Hardcoded or default secrets in config files are unsafe and should not be committed.

**Suggestion:**

- Use environment variables or a secret manager for:
  - `application.secret.key` (JWT)
  - `encryption.secret-key` and `encryption.algorithm`
- In config: `key: ${JWT_SECRET:}` with no default (or a default only for local dev, clearly documented).
- Ensure JWT key length is at least 256 bits (32 bytes) for HS256.

---

## High (Bugs & Consistency)

### 5. **Missing Report page (frontend)**

**Location:** `App.js` has `import Report from "./pages/Report"` and a route for `/report`, but **there is no `Report.js`** under `e-commerce-fe/src/pages/`.

**Issue:** Navigating to “Reports” or `/report` will cause a runtime error (failed import or missing component).

**Suggestion:**

- Either add `e-commerce-fe/src/pages/Report.js` (e.g. a placeholder “Reports” page), or
- Remove the Report route and the “Reports” link from the header until the feature exists.

---

### 6. **Redundant logic in getUserByEmail**

**Location:** `UserServiceImpl.getUserByEmail()`

**Issue:** `Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(...))` is redundant: `orElseThrow` already returns a `User` or throws. Wrapping in `Optional.ofNullable` and then `.orElse(null)` is confusing and can hide the throw.

**Suggestion:** Simplify to:

```java
@Override
public UserResponse getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    return mapToResponse(user);
}
```

---

### 7. **Generic RuntimeException for “User not found”**

**Location:** `UserServiceImpl.getUserById()` and `getUserByEmail()` use `new RuntimeException("User not found")`.

**Issue:** Controllers cannot return a structured 404 or a consistent error format if they rely on a generic exception.

**Suggestion:** Introduce a `UserNotFoundException` (or reuse a generic `ResourceNotFoundException`) and throw it instead. Add a `@ExceptionHandler` (or use a global handler) to return **404** and a consistent error body.

---

### 8. **Frontend axios interceptor and base URL**

**Location:** `e-commerce-fe/src/api/axios.js`

**Issue:** No `baseURL` is set; the app relies on `setupProxy.js` forwarding `/api` to `localhost:8081`. That’s fine for dev, but production builds need a clear API base URL.

**Suggestion:** Use an env-driven base URL when not using proxy, e.g.:

```javascript
const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || '',
  headers: { "Content-Type": "application/json" },
});
```

---

## Medium (Quality & Maintainability)

### 9. **Typo in package name: “entiry”**

**Location:** `com.omsoft.retail.user.entiry` (User, Auditable, etc.)

**Issue:** Package name typo (“entiry” instead of “entity”) is confusing and inconsistent with other modules.

**Suggestion:** Rename package to `entity` and update all imports. Do this in a dedicated refactor/PR to avoid breaking other branches.

---

### 10. **JWT secret key logging**

**Location:** `JwtUtil.generateToken()` logs `key: {}` with the secret (or part of it).

**Issue:** Logging secrets (or anything derived from them) is a security risk.

**Suggestion:** Remove the secret from logs. Log only subject (e.g. email) and maybe “token generated” for debugging.

---

### 11. **Global exception handling and user-management**

**Location:** `GlobalExceptionHandler` lives in **platform-common** and handles `org.omsoft.retail.exception.BusinessException`. **user-management-service** does not depend on platform-common and has its own `com.omsoft.retail.user.exception.BusinessException`.

**Issue:** Exceptions thrown from user-management may not be handled by the same contract (e.g. same HTTP status and body shape) if each service has its own handler or none.

**Suggestion:** Either:

- Have user-management depend on platform-common and use (or extend) the same exception types and handler, or
- Add a `@RestControllerAdvice` in user-management that maps its own exceptions (e.g. `AlreadyExistsException`, `UserNotFoundException`) to appropriate HTTP status and error DTOs so behavior is consistent and documented.

---

### 12. **PaymentController logic in payment-management-service**

**Location:** `PaymentController.pay()` uses a hardcoded amount limit (1,000,000) and does not persist or integrate with a real payment provider.

**Issue:** Fine for a stub, but the limit and behavior are magic numbers and not configurable.

**Suggestion:** Move the limit to configuration (e.g. `payment.max-amount`) and add a short comment that this is a stub. When integrating a real payment provider, replace this with a proper service call and idempotency/transaction handling.

---

### 13. **Protected route and token storage**

**Location:** `ProtectedRoute.js` and axios interceptor use `sessionStorage.getItem("token")`.

**Issue:** Session storage is cleared when the tab is closed; any refresh token or longer-lived session would need a different strategy. No handling for expired or invalid token (e.g. 401 from API).

**Suggestion:**

- On 401 from API, clear token and redirect to login (e.g. in an axios response interceptor).
- Document that the app uses session storage and that closing the tab logs the user out; if you need “remember me”, consider a secure, httpOnly cookie or a refresh token flow.

---

## Low (Nice to have)

### 14. **API response consistency**

**Issue:** Some endpoints return `ResponseEntity.ok(body)`, others return the body directly (e.g. `ProductController`). Delete returns 204/404, which is good.

**Suggestion:** Standardize on `ResponseEntity` for all REST endpoints so status codes and headers are explicit and consistent (e.g. 201 for create, 200 for get/update, 204 for delete).

---

### 15. **Frontend error handling**

**Issue:** Many `catch` blocks only `console.error(err)` and use `alert()` for success. Users get no structured error messages and no loading/disabled state in some flows.

**Suggestion:** Show user-friendly error messages (e.g. from `err.response?.data?.message` or a generic “Something went wrong”) and use a small toast/notification component instead of `alert()` where possible. Add loading/disabled state on submit where missing (e.g. Login already has this; ensure Users, Products, etc. do too).

---

### 16. **Validation on backend**

**Issue:** Some DTOs use `@Valid` (e.g. `UserRequest`, `LoginRequest`), but `LoginRequest` has no `@NotBlank` or `@Email` on email. Empty or invalid email can still reach the controller.

**Suggestion:** Add validation annotations to `LoginRequest` (e.g. `@NotBlank` and `@Email` on email, optionally `@NotBlank` on password) and handle `MethodArgumentNotValidException` in an exception handler to return 400 with field errors.

---

## Summary table

| #  | Area              | Severity  | Action |
|----|-------------------|-----------|--------|
| 1  | Login no password check | Critical | Validate credentials before issuing JWT; return 401 on failure |
| 2  | User update API   | Critical | Add PUT /api/user/{id} and UserService.updateUser |
| 3  | Passwords in API  | Critical | Remove password from UserResponse / do not expose decrypted password |
| 4  | Secrets in config | Critical | Use env vars / secret manager; no default production secrets |
| 5  | Missing Report.js | High     | Add Report page or remove route and nav link |
| 6  | getUserByEmail    | High     | Simplify to find or throw, then mapToResponse |
| 7  | User not found    | High     | Use UserNotFoundException (or similar) + 404 handler |
| 8  | Axios baseURL     | High     | Use REACT_APP_API_BASE_URL for production |
| 9  | Package “entiry”  | Medium   | Rename to “entity” and update imports |
| 10 | JWT in logs       | Medium   | Stop logging secret/key material |
| 11 | Exception handling| Medium   | Align user-management with global handler or add local advice |
| 12 | Payment stub      | Medium   | Configurable limit; comment; replace with real integration later |
| 13 | Token/401         | Medium   | On 401, clear token and redirect to login |
| 14 | Response format   | Low      | Standardize on ResponseEntity and status codes |
| 15 | Frontend errors   | Low      | User-facing messages and toasts instead of only alert |
| 16 | LoginRequest validation | Low | Add @Valid and field constraints; handle validation errors |

Implementing the **Critical** and **High** items first will materially improve security and correctness; the rest will improve robustness and maintainability.
