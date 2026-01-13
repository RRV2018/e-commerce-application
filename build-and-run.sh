#!/bin/bash
set -e

echo "Building Docker images..."

docker build -t api-gateway api-gateway-service/
docker build -t property-config property-config-server/
docker build -t user-management user-management-service/
docker build -t product-catalog product-catalog-service/
docker build -t inventory-management inventory-management-service/
docker build -t order-management order-management-service/
docker build -t payment-management payment-management-service/
docker build -t eureka-discovery eureka-discovery-server/
docker build -t app-monitoring-admin app-monitoring-admin-server/

echo "Starting containers..."
docker compose down
docker compose up --build
