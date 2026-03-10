# E-Commerce Application

A full-stack e-commerce platform built with a **microservices architecture**: React frontend, Spring Boot backend services, API Gateway, service discovery, and shared PostgreSQL and Kafka.

## Overview

- **Backend:** Java 17, Spring Boot 3.x, Spring Cloud 2025.0.0  
- **Frontend:** React 18, React Router, Axios  
- **Infrastructure:** Docker Compose (PostgreSQL, Kafka, Eureka, Config Server, Grafana, Loki, Promtail, Nginx)  
- **Database:** PostgreSQL with Flyway migrations  

## Quick Start

1. **Prerequisites:** Docker & Docker Compose, Node.js 18+ (for local frontend), Java 17 & Maven (for local backend).

2. **Run with Docker (recommended):**
   ```bash
   # From project root; ensure .env is present (see docs/CONFIGURATION.md)
   docker compose up --build
   ```
   - API Gateway: http://localhost:8081  
   - Eureka: http://localhost:8761  
   - Config Server: http://localhost:8888  
   - Frontend (via Nginx): http://localhost:80  
   - Grafana: http://localhost:3000 (default login: admin / admin; Loki datasource pre-configured)  

3. **Run frontend locally (dev):**
   ```bash
   cd e-commerce-fe
   npm install
   npm start
   ```
   Uses proxy: `/api` → Gateway (8081), `/auth` → User Service (8082). Backend services must be running.

4. **Build backend (Maven):**
   ```bash
   mvn clean install
   ```

## Documentation

| Document | Description |
|----------|-------------|
| [Getting Started](docs/GETTING_STARTED.md) | Detailed setup, run options, and troubleshooting |
| [Architecture](docs/ARCHITECTURE.md) | Services, ports, API routes, and data flow |
| [Configuration](docs/CONFIGURATION.md) | Environment variables and config server |

## Project Structure

```
e-commerce-application-new/
├── api-gateway-service       # Spring Cloud Gateway (port 8081)
├── eureka-discovery-server   # Service discovery (port 8761)
├── property-config-server    # Spring Cloud Config (port 8888)
├── user-management-service   # Users & auth (port 8082)
├── product-catalog-service   # Products & categories (port 8083)
├── order-management-service  # Orders (port 8084)
├── inventory-management-service # Inventory (port 8085)
├── payment-management-service   # Payments (port 8086)
├── app-monitoring-admin-server # Spring Boot Admin (port 8087)
├── notification-management-service
├── platform-common           # Shared library
├── logging-common            # Logging utilities
├── flywaydb-scripts          # DB migrations
├── e-commerce-fe             # React SPA
├── config_properties         # Config server YAML files
├── docker                    # Dockerfiles per service
├── scripts                   # Docker / utility scripts
├── docker-compose.yml
├── pom.xml                   # Maven parent POM
└── docs/                     # Documentation
```

## License

Internal / project-specific.
