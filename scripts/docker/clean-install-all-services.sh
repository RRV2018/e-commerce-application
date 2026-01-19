#!/usr/bin/env bash
set -e

echo "==============================="
echo "Maven clean package (all modules)"
echo "==============================="
mvn clean package -DskipTests

echo "==============================="
echo "Stopping existing containers..."
echo "==============================="
docker compose down

echo
echo "==============================="
echo "Building common base image..."
echo "==============================="
docker build -t omsoft/common-base-image:1.0 docker/base
docker tag omsoft/common-base-image:1.0 omsoft/common-base-image:latest

echo
echo "==============================="
echo "Building microservice images..."
echo "==============================="

docker build -f docker/api-gateway-service/Dockerfile -t api-gateway-service .
docker build -f docker/property-config-server/Dockerfile -t property-config-server .
docker build -f docker/user-management-service/Dockerfile -t user-management-service .
docker build -f docker/product-catalog-service/Dockerfile -t product-catalog-service .
docker build -f docker/inventory-management-service/Dockerfile -t inventory-management-service .
docker build -f docker/order-management-service/Dockerfile -t order-management-service .
docker build -f docker/payment-management-service/Dockerfile -t payment-management-service .
docker build -f docker/eureka-discovery-server/Dockerfile -t eureka-discovery-server .
docker build -f docker/app-monitoring-admin-server/Dockerfile -t app-monitoring-admin-server .

echo
echo "==============================="
echo "Starting all containers..."
echo "==============================="
docker compose pull
docker compose up
