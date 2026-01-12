# ---------- Build all modules ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY api-gateway-service api-gateway-service
COPY logging-common logging-common
COPY property-config-server property-config-server
COPY user-management-service user-management-service
COPY product-catalog-service product-catalog-service
COPY inventory-management-service inventory-management-service
COPY order-management-service order-management-service
COPY payment-management-service payment-management-service
COPY app-monitoring-admin-server app-monitoring-admin-server
COPY notification-management-service notification-management-service
COPY platform-common platform-common
COPY eureka-discovery-server eureka-discovery-server


RUN mvn clean package -DskipTests

# ---------- Runtime (example: api-gateway) ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /build/api-gateway-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
