$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
podman compose stop e-commerce-fe
podman compose rm -f e-commerce-fe

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice image..."
Write-Host "==============================="
podman build --no-cache -f docker/e-commerce-fe/Dockerfile -t e-commerce-fe ./e-commerce-fe

Write-Host ""
Write-Host "==============================="
Write-Host "Starting e-commerce-fe containers..."
Write-Host "==============================="
podman start e-commerce-fe
