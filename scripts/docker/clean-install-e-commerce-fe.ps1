$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
docker compose stop e-commerce-fe
docker compose rm -f e-commerce-fe

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice image..."
Write-Host "==============================="
docker build --no-cache -f docker/e-commerce-fe/Dockerfile -t e-commerce-fe ./e-commerce-fe

Write-Host ""
Write-Host "==============================="
Write-Host "Starting e-commerce-fe containers..."
Write-Host "==============================="
docker start e-commerce-fe
