# Configuration

## Environment Variables (Docker Compose)

These are referenced in `docker-compose.yml` and should be set in a `.env` file in the project root (or in the shell) when running with Docker.

| Variable | Example | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `docker` | Active Spring profile (e.g. for config server URLs) |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/postgres` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | DB password |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | Kafka broker list |
| `KEYCLOAK_AUTH_SERVER_URL` | `http://keycloak:9080` | Keycloak (optional) |
| `EUREKA_DEFAULT_ZONE` | `http://eureka-discovery-server:8761/eureka` | Eureka server URL |
| `CONFIG_SERVER_URL` | `http://property-config-server:8888` | Config Server URL |
| `ENCRYPTION_SECRET_KEY` | *(secret)* | Used by user-management-service for encryption |
| `TIME_ZONE` | `Asia/Calcutta` | Optional time zone |

### Example `.env`

```env
SPRING_PROFILES_ACTIVE=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KEYCLOAK_AUTH_SERVER_URL=http://keycloak:9080
EUREKA_DEFAULT_ZONE=http://eureka-discovery-server:8761/eureka
CONFIG_SERVER_URL=http://property-config-server:8888
TIME_ZONE=Asia/Calcutta
ENCRYPTION_SECRET_KEY=your-secret-key-here
```

**Security:** Do not commit real secrets. Use a local `.env` (gitignored) or a secret manager in production.

## Config Server (Spring Cloud Config)

- **Server:** `property-config-server` (port 8888).  
- **Config files:** Stored in `config_properties/` (mounted or packaged as needed):
  - `application.yml` – shared (Kafka, Eureka, JPA, logging, Resilience4j, etc.)
  - `api-gateway-service.yml` – gateway port and Swagger URLs
  - `user-management-service.yml`
  - `product-catalog-service.yml`
  - `order-management-service.yml`
  - `inventory-management-service.yml`
  - `payment-management-service.yml`
  - `notification-management-service.yml`

Services that use Config Client fetch their config from `CONFIG_SERVER_URL` and optionally use `SPRING_PROFILES_ACTIVE` for profile-specific overrides.

## Frontend

- **Dev proxy:** `e-commerce-fe/src/setupProxy.js`
  - `/api` → `http://localhost:8081` (API Gateway)
  - `/auth` → `http://localhost:8082` (User Management)
- **Auth:** Token is stored in `sessionStorage` under key `token`; axios interceptor adds `Authorization: Bearer <token>` for requests that do not start with `/api/auth`.

## Backend Application Properties

- **JWT:** In `application.yml`, `application.secret.key` is used for JWT signing. Prefer overriding via environment or secret manager.
- **File upload:** `spring.servlet.multipart.max-file-size` and `max-request-size` are set to 200MB in shared config.
- **Eureka:** Clients use `EUREKA_DEFAULT_ZONE` or default `http://localhost:8761/eureka`.
- **Kafka:** `spring.kafka.bootstrap-servers` and consumer/producer settings are in `application.yml`; override with `SPRING_KAFKA_BOOTSTRAP_SERVERS` in Docker.

For per-service tuning (ports, DB names, etc.), edit the corresponding file in `config_properties/` and ensure the Config Server can read it.
