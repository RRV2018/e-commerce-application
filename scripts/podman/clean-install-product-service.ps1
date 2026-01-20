$ErrorActionPreference = "Stop"

Write-Host "==============================="
Write-Host "Maven clean package (all modules)"
Write-Host "==============================="
mvn clean package -DskipTests -pl product-catalog-service -am

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
podman compose stop product-catalog-service
podman compose rm -f product-catalog-service

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice images..."
Write-Host "==============================="

podman build -f docker/product-catalog-service/Dockerfile -t product-catalog-service .

Write-Host ""
Write-Host "==============================="
Write-Host "Starting all containers..."
Write-Host "==============================="
podman start product-catalog-service
