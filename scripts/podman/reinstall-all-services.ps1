$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
podman compose down

Write-Host ""
Write-Host "==============================="
Write-Host "Building common base image..."
Write-Host "==============================="
podman build -t omsoft/common-base-image:1.0 docker/base
podman tag omsoft/common-base-image:1.0 omsoft/common-base-image:latest

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice images..."
Write-Host "==============================="

podman build -f docker/api-gateway-service/Dockerfile -t api-gateway-service .
podman build -f docker/property-config-server/Dockerfile -t property-config-server .
podman build -f docker/user-management-service/Dockerfile -t user-management-service .
podman build -f docker/product-catalog-service/Dockerfile -t product-catalog-service .
podman build -f docker/inventory-management-service/Dockerfile -t inventory-management-service .
podman build -f docker/order-management-service/Dockerfile -t order-management-service .
podman build -f docker/payment-management-service/Dockerfile -t payment-management-service .
podman build -f docker/eureka-discovery-server/Dockerfile -t eureka-discovery-server .
podman build -f docker/app-monitoring-admin-server/Dockerfile -t app-monitoring-admin-server .

Write-Host "FE Application==============================="
podman build --no-cache -f docker/e-commerce-fe/Dockerfile -t e-commerce-fe ./e-commerce-fe

Write-Host ""
Write-Host "==============================="
Write-Host "Starting all containers..."
Write-Host "==============================="
podman-compose pull
podman compose up -d
