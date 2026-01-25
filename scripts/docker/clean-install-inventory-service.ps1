$ErrorActionPreference = "Stop"

Write-Host "==============================="
Write-Host "Maven clean package (all modules)"
Write-Host "==============================="
mvn clean package -DskipTests -pl inventory-management-service -am

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
docker compose stop inventory-management-service
docker compose rm -f inventory-management-service

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice images..."
Write-Host "==============================="

docker build --no-cache -f docker/inventory-management-service/Dockerfile -t inventory-management-service .

Write-Host ""
Write-Host "==============================="
Write-Host "Starting all containers..."
Write-Host "==============================="
docker compose start inventory-management-service