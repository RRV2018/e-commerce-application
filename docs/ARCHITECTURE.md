# Architecture

## High-Level Overview

The application is a **microservices-based e-commerce platform**:

- **Frontend:** Single-page application (React) talking to an API Gateway.
- **API Gateway:** Single entry point; routes to backend services and applies JWT authentication.
- **Service discovery:** Netflix Eureka; services register and are resolved by name (e.g. `lb://user-management-service`).
- **Configuration:** Spring Cloud Config Server; per-service YAML in `config_properties/`.
- **Data:** PostgreSQL (one logical DB; schema per domain via Flyway). Kafka for async messaging.
- **Observability:** Spring Boot Admin, Actuator, Grafana, Loki, Promtail.

## Services and Ports

| Service | Port | Description |
|--------|------|-------------|
| **api-gateway-service** | 8081 | Spring Cloud Gateway; routes `/api/*` to downstream services; JWT filter; Swagger UI aggregation |
| **eureka-discovery-server** | 8761 | Service discovery (Eureka server) |
| **property-config-server** | 8888 | Spring Cloud Config Server |
| **user-management-service** | 8082 | Users, registration, login, JWT issuance |
| **product-catalog-service** | 8083 | Products, categories, file uploads |
| **order-management-service** | 8084 | Orders and order items |
| **inventory-management-service** | 8085 | Inventory |
| **payment-management-service** | 8086 | Payments |
| **app-monitoring-admin-server** | 8087 | Spring Boot Admin UI |
| **notification-management-service** | — | Notifications (Kafka, etc.) |
| **e-commerce-fe** | 3001 | React app (when run in Docker) |
| **nginx** | 80 | Reverse proxy for frontend |
| **postgres** | 5433→5432 | PostgreSQL 15 |
| **grafana** | 3000 | Dashboards / Loki datasource |
| **loki** | 3100 | Log aggregation |
| **kafka** | 9092 | Apache Kafka |
| **zookeeper** | 2181 | For Kafka |

## API Gateway Routes

All client requests to backend APIs go through the gateway at port **8081** under `/api` (and `/api/auth` for auth). The gateway uses Eureka to resolve service names (`lb://...`).

| Path pattern | Target service | Notes |
|--------------|----------------|--------|
| `/api/user/**`, `/api/auth/**` | user-management-service | Login, user CRUD, auth |
| `/api/products/**`, `/api/products/file/**` | product-catalog-service | Products, categories, file upload |
| `/api/order/**` | order-management-service | Orders |
| `/api/inventory/**` | inventory-management-service | Inventory |
| `/api/payment/**` | payment-management-service | Payments |

Swagger/OpenAPI is aggregated at:

- **Swagger UI:** http://localhost:8081/swagger-ui/index.html  
- Service-specific docs are linked there (User, Products, Order, Inventory, Payment).

Public endpoints (no JWT): e.g. `/api/auth/login`, `/actuator/*`. All other `/api/*` paths go through the JWT filter.

## Frontend Structure

- **Framework:** React 18, React Router 6.
- **HTTP:** Axios instance in `src/api/axios.js`; adds `Authorization: Bearer <token>` for non-auth requests; base URL is relative so proxy applies.
- **Proxy (dev):** `setupProxy.js` sends `/api` → Gateway (8081), `/auth` → User Service (8082).
- **Routes:**
  - `/login` – Login
  - `/dashboard` – Dashboard
  - `/products` – Products
  - `/users` – User management
  - `/orders` – Orders
  - `/fileOperation` – File operations
  - `/report` – Reports
- **Auth:** JWT stored in `sessionStorage`; `ProtectedRoute` guards pages; login returns token from User Service (via `/auth` then gateway or direct proxy).

## Backend Stack (per service)

- **Java 17**, **Spring Boot 3.x**, **Spring Cloud 2025.0.0**
- **Persistence:** JPA/Hibernate, PostgreSQL
- **Migrations:** Flyway (scripts in `flywaydb-scripts`)
- **Service-to-service:** OpenFeign (optional), Eureka client
- **Resilience:** Resilience4j (e.g. circuit breaker in config)
- **Messaging:** Spring Kafka
- **Docs:** SpringDoc OpenAPI (v3) per service, aggregated at gateway
- **Monitoring:** Spring Boot Admin client, Actuator

## Shared Modules

- **platform-common** – Shared DTOs, utilities, or client stubs.
- **logging-common** – Common logging configuration or utilities.

## Data Flow (Simplified)

1. **Browser** → Nginx (or CRA dev server) → **API Gateway (8081)** for `/api/*`.
2. **Gateway** validates JWT (except public paths), then routes by path to the right service via **Eureka**.
3. **Microservices** use **Config Server** for properties, **PostgreSQL** for data, **Kafka** for events.
4. **Flyway** runs at startup (or via dedicated container) to apply DB migrations.

## Database (Flyway)

Migrations live in `flywaydb-scripts/src/main/resources/db/migration/`:

- `V1__create_user_table.sql`
- `V2__create_category_table.sql`
- `V3__create_products_table.sql`
- `V4__create_address_table.sql`
- `V5__create_orders_table.sql`
- `V6__create_order_items_table.sql`
- `V7__create_inventory_table.sql`
- `V12__create_spring_batch_tables.sql`

All services that need the DB use the same PostgreSQL instance; schema is shared and managed via these scripts.

## Security (Current)

- **Auth:** JWT issued by user-management-service (login). Gateway filter validates JWT on `/api/*` (except login/actuator).
- **Secrets:** JWT secret and encryption keys are in config/env; for production, use environment variables or a secret manager (see [CODE_REVIEW_SUGGESTIONS.md](../CODE_REVIEW_SUGGESTIONS.md) if present).

For hardening and known review items (e.g. password validation on login, not returning passwords in APIs), refer to the code review document in the repo.
