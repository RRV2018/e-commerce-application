# Getting Started

This guide walks you through setting up and running the E-Commerce application.

## Prerequisites

- **Docker & Docker Compose** – for running the full stack in containers  
- **Java 17** – for building/running backend services locally  
- **Maven 3.8+** – for building the multi-module project  
- **Node.js 18+** and **npm** – for running the React frontend locally  

## Option 1: Run Everything with Docker Compose

1. **Clone the repository** (if not already done).

2. **Ensure `.env` exists** in the project root with the variables used by `docker-compose.yml`. See [Configuration](CONFIGURATION.md) for a full list. Example:
   ```env
   SPRING_PROFILES_ACTIVE=docker
   SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=postgres
   SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
   EUREKA_DEFAULT_ZONE=http://eureka-discovery-server:8761/eureka
   CONFIG_SERVER_URL=http://property-config-server:8888
   ENCRYPTION_SECRET_KEY=<your-secret>
   ```

3. **Start the stack:**
   ```bash
   docker compose up --build
   ```

4. **Wait for services to become healthy** (Eureka, Config Server, then others). Access:
   - **Web app:** http://localhost (Nginx) or http://localhost:3001 (e-commerce-fe container)
   - **API Gateway:** http://localhost:8081
   - **Swagger UI (aggregated):** http://localhost:8081/swagger-ui/index.html
   - **Eureka Dashboard:** http://localhost:8761
   - **Grafana:** http://localhost:3000

### Using the build scripts

- **PowerShell (Windows):** `.\build-and-run.ps1`  
- **Bash (Linux/macOS):** `./build-and-run.sh`  

Note: These scripts build images from service directories and then run `docker compose up --build`. The Compose file itself uses Dockerfiles under `docker/`; ensure paths and image names match if you customize.

## Option 2: Run Backend Locally (Maven)

1. **Start infrastructure only** (PostgreSQL, Kafka, Zookeeper, Eureka, Config Server) – either run selected services via Docker or use local installs.

2. **Point config to your environment** (e.g. `config_properties` or Config Server). Set:
   - `EUREKA_DEFAULT_ZONE` (e.g. `http://localhost:8761/eureka`)
   - `CONFIG_SERVER_URL` (e.g. `http://localhost:8888`) if using Config Server
   - `SPRING_DATASOURCE_*` and `SPRING_KAFKA_BOOTSTRAP_SERVERS` as needed

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Start services in order:**
   - Eureka Discovery Server  
   - Property Config Server  
   - User Management Service  
   - Product Catalog, Order, Inventory, Payment, API Gateway, etc.

   Example (from project root):
   ```bash
   mvn -pl eureka-discovery-server spring-boot:run
   mvn -pl property-config-server spring-boot:run
   mvn -pl user-management-service spring-boot:run
   # ... then remaining services
   ```

### Running product-catalog-service from your IDE (with Kafka in Docker/Podman)

When you run **product-catalog-service** from your IDE, it runs on your machine (host), while Kafka runs inside Docker or Podman. The hostname `kafka` only works inside the container network, so the app must use **localhost** to reach Kafka.

1. **Start Kafka (and Zookeeper) in Docker or Podman** so that port **9092** is exposed on the host:
   ```bash
   docker compose up -d zookeeper kafka
   # or: podman compose up -d zookeeper kafka
   ```
   Ensure `docker-compose.yml` (or your compose file) maps Kafka port `9092:9092`.

2. **Run product-catalog-service from your IDE** using one of these:

   - **Option A – Spring profile `local`** (recommended):
     - In your run configuration, set **Active profiles** to: `local`
     - product-catalog-service has `application-local.yml` with `spring.kafka.bootstrap-servers: localhost:9092`.

   - **Option B – Environment variable:**
     - In your run configuration, add an **Environment variable**:
       - Name: `SPRING_KAFKA_BOOTSTRAP_SERVERS`
       - Value: `localhost:9092`

After this, product-catalog-service started from the IDE will connect to Kafka running in Docker/Podman at `localhost:9092`.

## Option 3: Run Frontend Locally (Development)

1. **Backend must be running** – at least API Gateway (8081) and User Management (8082) for login and API calls.

2. **Install and start the React app:**
   ```bash
   cd e-commerce-fe
   npm install
   npm start
   ```

3. **Proxy behavior** (`setupProxy.js`):
   - `/api` → `http://localhost:8081` (API Gateway)
   - `/auth` → `http://localhost:8082` (User Management)

4. Open http://localhost:3000 (or the port shown by Create React App). Log in via the app; the frontend sends requests to `/api` and `/auth` with JWT in headers for protected routes.

## Run Database Migrations (Flyway)

With Docker Compose, the `flyway` service runs automatically after PostgreSQL is healthy and applies scripts from `flywaydb-scripts/src/main/resources/db/migration/`.

To run Flyway manually against a running Postgres instance:
```bash
docker run --rm --network host -v $(pwd)/flywaydb-scripts/src/main/resources/db/migration:/flyway/sql flyway/flyway:10 \
  -url=jdbc:postgresql://localhost:5433/postgres -user=postgres -password=postgres -baselineOnMigrate=true migrate
```
Adjust host/port/user/password to match your environment.

## Troubleshooting

- **Services not registering in Eureka:** Ensure Eureka and Config Server (if used) start first and are reachable; check `EUREKA_DEFAULT_ZONE` and `CONFIG_SERVER_URL`.
- **502 / connection refused from frontend:** Confirm API Gateway (8081) and, for login, User Management (8082) are up; check proxy target in `e-commerce-fe/src/setupProxy.js`.
- **Database errors:** Verify PostgreSQL is running and URL/credentials match config; run Flyway if schema is missing.
- **Kafka errors in product-catalog-service (e.g. "No resolvable bootstrap urls" when running from IDE):** Start Kafka in Docker/Podman (`docker compose up -d zookeeper kafka`), then run product-catalog-service with profile `local` or set `SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092`. See [Running product-catalog-service from your IDE](#running-product-catalog-service-from-your-ide-with-kafka-in-dockerpodman) above.

For more detail on ports and service roles, see [Architecture](ARCHITECTURE.md).
