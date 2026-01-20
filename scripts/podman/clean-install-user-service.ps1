$ErrorActionPreference = "Stop"

Write-Host "==============================="
Write-Host "Maven clean package (all modules)"
Write-Host "==============================="
mvn clean package -DskipTests -pl user-management-service -am

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
podman compose stop user-management-service
podman compose rm -f user-management-service

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice images..."
Write-Host "==============================="

podman build -f docker/user-management-service/Dockerfile -t user-management-service .

Write-Host ""
Write-Host "==============================="
Write-Host "Starting all containers..."
Write-Host "==============================="
podman start user-management-service
