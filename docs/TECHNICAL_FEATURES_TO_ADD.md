# Technical Features to Add – E-Commerce Application

This document lists **technical features that are missing or recommended** for the project, based on codebase analysis and the existing [CODE_REVIEW_SUGGESTIONS.md](../CODE_REVIEW_SUGGESTIONS.md). Items are grouped by area and priority.

---

## 1. Security & Authentication

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **Password validation on login** | Missing | Login currently issues JWT for any email without checking password. Use `AuthenticationManager` + `UserDetailsService`, return **401** for invalid credentials. |
| **Remove passwords from API responses** | Missing | `UserResponse` and `mapToResponse()` must not expose (decrypted) password. Remove field or use placeholder (e.g. `********`). |
| **Secrets from environment / secret manager** | Missing | JWT secret and encryption keys are in config files. Use env vars (e.g. `JWT_SECRET`, `ENCRYPTION_SECRET_KEY`) or a secret manager; no default production secrets in repo. |
| **Refresh token / longer sessions** | Missing | Only access token in `sessionStorage`; no refresh flow. Add refresh token endpoint and storage strategy (e.g. httpOnly cookie or secure storage) for “remember me” and longer sessions. |
| **Role-based access control (RBAC)** | Partial | `Role` enum (ADMIN, CUSTOMER) and `X-User-Role` header exist but APIs are not protected by role. Add `@PreAuthorize("hasRole('ADMIN')")` (or method security) for admin-only endpoints (e.g. user list, product upload). |
| **401 handling in frontend** | Missing | On 401 from API, clear token and redirect to login (e.g. axios response interceptor). |
| **Stop logging JWT/secret material** | Missing | Remove any logging of secret or key in `JwtUtil` or config. |

---

## 2. API Completeness & Consistency

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **User update API (PUT)** | Missing | Frontend calls `PUT /api/user/:id` but backend has no PUT endpoint or `UserService.updateUser()`. Add PUT and update logic (avoid exposing/overwriting password incorrectly). |
| **Pagination on list APIs** | Partial | Only **product** service has `Pageable`/`Page`. **User** (`getAllUsers`) and **Order** (`getOrders`) return full lists. Add pagination (e.g. `?page=0&size=20`) and consistent response shape (e.g. `{ content, totalElements, totalPages }`). |
| **API versioning** | Missing | No `/v1/` or version header. Introduce path or header versioning (e.g. `/api/v1/user`) for future non-breaking changes. |
| **Structured error responses** | Partial | `GlobalExceptionHandler` in platform-common; user-management has its own exceptions and may not align. Unify exception types and handlers so all services return same error schema (e.g. `ErrorResponse`) and status codes. |
| **Resource not found (404)** | Partial | Use `UserNotFoundException` (or generic `ResourceNotFoundException`) + `@ExceptionHandler` returning 404 instead of generic `RuntimeException`. |
| **Response format consistency** | Partial | Standardize on `ResponseEntity` and status codes: **201** for create, **200** for get/update, **204** for delete, **404** for not found. |
| **Request validation** | Partial | Add `@Valid` and constraints (e.g. `@NotBlank`, `@Email`) on `LoginRequest` and other DTOs; add handler for `MethodArgumentNotValidException` returning 400 with field errors. |

---

## 3. Frontend

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **Report page** | Missing | `App.js` imports `Report` and has `/report` route but **Report.js** does not exist. Add a Report page or remove route and nav link. |
| **API base URL for production** | Missing | No `baseURL` in axios; app relies on proxy. Use `process.env.REACT_APP_API_BASE_URL` for production builds. |
| **User-facing error handling** | Weak | Many catches only `console.error` + `alert()`. Use `err.response?.data?.message` and a toast/notification component; show loading/disabled state on submit. |
| **Frontend unit / integration tests** | Minimal | Only default `App.test.js`. Add tests for critical flows (login, protected routes, API error handling) and key components (e.g. with React Testing Library). |
| **E2E tests** | Missing | No Cypress/Playwright (or similar). Add E2E for main user journeys (login → browse products → place order). |
| **State management** | None | No Redux/Zustand/Context for shared state. Consider for user, cart, or global UI state if the app grows. |
| **Environment-based config** | Partial | Document and use `.env` (e.g. `REACT_APP_API_BASE_URL`) for dev vs prod. |

---

## 4. Backend Resilience & Performance

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **Caching** | Missing | No Redis or Caffeine. Add caching for read-heavy, stable data (e.g. product catalog, categories) with TTL and cache invalidation on update. |
| **Rate limiting** | Missing | No rate limiter on gateway or services. Add rate limiting (e.g. Resilience4j or Bucket4j) per user/IP to protect against abuse. |
| **Circuit breaker usage** | Config only | Resilience4j is in config (e.g. `userServiceCB`); verify it is applied on Feign/client calls and document behavior. |
| **Retry policy** | Dependency only | `spring-retry` is present; ensure retries are configured for external calls (DB, Kafka, other services) where appropriate. |
| **Distributed tracing** | Missing | No Sleuth/Micrometer Tracing or OpenTelemetry. Add trace IDs (e.g. `X-Request-Id` or W3C Trace Context) for request correlation across services. |

---

## 5. Data & File Handling

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **Audit fields** | Missing | No `created_at`/`updated_at` (or `createdBy`/`updatedBy`) in DB/entities. Add audit columns and use `@CreatedDate`/`@LastModifiedDate` (Spring Data JPA) or Flyway migration. |
| **Soft delete** | Missing | Deletes are likely hard deletes. Consider `deleted_at` (or `is_deleted`) and filter in queries for recoverability and compliance. |
| **File upload validation** | Weak | Product file upload uses `MultipartFile` but no explicit check for file type/size (beyond Spring max size). Validate extension and content type; restrict to allowed types (e.g. CSV, XLSX). |
| **Idempotency** | Missing | Payment and order placement have no idempotency key. For payments and critical mutations, accept `Idempotency-Key` and return same result on replay. |

---

## 6. DevOps & Quality

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **CI/CD pipeline** | Missing | No GitHub Actions, GitLab CI, Jenkinsfile, or Azure Pipelines. Add pipeline for: build, unit tests, integration tests, and optional deploy (e.g. Docker build/push). |
| **Integration tests** | Partial | Some controller/service tests exist; add tests that start application context and call APIs (e.g. `@SpringBootTest` + `MockMvc` or `TestRestTemplate`). |
| **Test coverage reporting** | Unknown | Add JaCoCo (backend) and frontend coverage (e.g. Jest with coverage) and enforce minimum in CI. |
| **Dependency and security scanning** | Missing | Add OWASP Dependency Check, Snyk, or Dependabot; fail or warn on known vulnerabilities. |
| **API contract testing** | Missing | Consider Spring Cloud Contract or Pact for consumer-driven contracts between frontend/gateway and services. |
| **Database migration strategy** | Present | Flyway is used; ensure all envs run migrations in a controlled way (already documented). |

---

## 7. Observability & Operations

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **Structured logging** | Partial | Logging exists; consider JSON logging and consistent fields (e.g. `requestId`, `userId`, `action`) for Loki/Grafana. |
| **Health checks** | Present | Actuator health is used; ensure readiness/liveness are used by orchestrator (e.g. Kubernetes) if applicable. |
| **Metrics** | Partial | Actuator and Spring Boot Admin; add custom business metrics (e.g. orders placed, login failures) and dashboards in Grafana. |
| **Alerting** | Unknown | Define alerts (e.g. error rate, latency, queue lag) in Grafana or Prometheus if added. |

---

## 8. Documentation & Maintainability

| Feature | Status | Recommendation |
|--------|--------|----------------|
| **OpenAPI/Swagger** | Present | SpringDoc is used and aggregated at gateway; keep descriptions and examples up to date. |
| **README and runbooks** | Present | Docs added (Getting Started, Architecture, Configuration); add runbook for common failures and rollback. |
| **Package naming** | Bug | Fix typo `com.omsoft.retail.user.entiry` → `entity` and update imports. |
| **Payment stub** | Placeholder | Payment logic is stub with hardcoded limit. Move limit to config (`payment.max-amount`); document as stub and plan real provider integration with idempotency. |

---

## Priority Summary

| Priority | Focus |
|----------|--------|
| **P0 (Critical)** | Password validation on login; remove passwords from API; user update API; secrets from env/secret manager. |
| **P1 (High)** | Report page or remove route; pagination for users/orders; 401 handling and optional refresh token; RBAC on admin APIs; axios base URL; unified exception handling and 404. |
| **P2 (Medium)** | Rate limiting; caching; file upload validation; audit fields; CI/CD; distributed tracing; frontend error UX and tests. |
| **P3 (Nice to have)** | API versioning; soft delete; idempotency; E2E tests; state management; contract tests; alerting. |

Implementing **P0** and **P1** will materially improve security, correctness, and usability. The rest will improve scalability, operability, and long-term maintainability.
