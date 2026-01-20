$ErrorActionPreference = "Stop"

Write-Host "==============================="
Write-Host "Maven clean package (all modules)"
Write-Host "==============================="
mvn clean package -DskipTests -pl app-monitoring-admin-server -am

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
podman compose stop app-monitoring-admin-server
podman compose rm -f app-monitoring-admin-server

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice images..."
Write-Host "==============================="

podman build --no-cache -f docker/app-monitoring-admin-server/Dockerfile -t app-monitoring-admin-server .

Write-Host ""
Write-Host "==============================="
Write-Host "Starting all containers..."
Write-Host "==============================="
podman start app-monitoring-admin-server
