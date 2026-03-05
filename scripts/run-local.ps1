# Run infrastructure in Docker, then all microservices + frontend locally (each in its own window).
# Usage: .\scripts\run-local.ps1
# Prereqs: Docker running, Maven, Node.js. Ensure .env exists with ENCRYPTION_SECRET_KEY and JWT_SECRET.

$ErrorActionPreference = "Stop"
$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $ProjectRoot

# Load .env if present
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match "^\s*([^#][^=]+)=(.*)$") {
            [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), "Process")
        }
    }
}

# Local env for services (override Docker-specific values)
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5433/postgres"
$env:EUREKA_DEFAULT_ZONE = "http://localhost:8761/eureka"
$env:CONFIG_SERVER_URL = "http://localhost:8888"
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9093"
$env:SPRING_PROFILES_ACTIVE = "docker"

if (-not $env:ENCRYPTION_SECRET_KEY) { $env:ENCRYPTION_SECRET_KEY = "encryption-key-16" }
if (-not $env:JWT_SECRET) { $env:JWT_SECRET = "mySecretKeyForJwtTokenGeneration123" }

Write-Host "=== Starting infrastructure in Docker (Postgres, Zookeeper, Kafka, Flyway only) ===" -ForegroundColor Cyan
docker compose up -d postgres zookeeper kafka flyway

Write-Host "Waiting 25s for infra to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 25

$mvnRun = "mvn -q -pl `"MODULE`" spring-boot:run"
$services = @(
    @{ Name = "eureka-discovery-server"; Module = "eureka-discovery-server" },
    @{ Name = "property-config-server"; Module = "property-config-server" },
    @{ Name = "user-management-service"; Module = "user-management-service" },
    @{ Name = "product-catalog-service"; Module = "product-catalog-service"; ExtraEnv = "SPRING_PROFILES_ACTIVE=local,docker" },
    @{ Name = "order-management-service"; Module = "order-management-service" },
    @{ Name = "inventory-management-service"; Module = "inventory-management-service" },
    @{ Name = "payment-management-service"; Module = "payment-management-service" },
    @{ Name = "api-gateway-service"; Module = "api-gateway-service" },
    @{ Name = "app-monitoring-admin-server"; Module = "app-monitoring-admin-server" }
)

foreach ($svc in $services) {
    $cmd = $mvnRun -replace "MODULE", $svc.Module
    $profiles = $env:SPRING_PROFILES_ACTIVE
    if ($svc.ExtraEnv) {
        $profiles = ($svc.ExtraEnv -split "=")[1]
    }
    Write-Host "Starting $($svc.Name) in new window..." -ForegroundColor Green
    $arg = "cd '$ProjectRoot'; `$env:SPRING_DATASOURCE_URL='$env:SPRING_DATASOURCE_URL'; `$env:EUREKA_DEFAULT_ZONE='$env:EUREKA_DEFAULT_ZONE'; `$env:CONFIG_SERVER_URL='$env:CONFIG_SERVER_URL'; `$env:SPRING_KAFKA_BOOTSTRAP_SERVERS='$env:SPRING_KAFKA_BOOTSTRAP_SERVERS'; `$env:SPRING_PROFILES_ACTIVE='$profiles'; `$env:ENCRYPTION_SECRET_KEY='$env:ENCRYPTION_SECRET_KEY'; `$env:JWT_SECRET='$env:JWT_SECRET'; $cmd"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $arg
    Start-Sleep -Seconds 15
}

Write-Host "Starting frontend (e-commerce-fe) in new window..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$ProjectRoot\e-commerce-fe'; npm start"

Write-Host ""
Write-Host "Done. All services and frontend are starting in separate windows." -ForegroundColor Cyan
Write-Host "Frontend: http://localhost:3000  |  API Gateway: http://localhost:8081  |  Eureka: http://localhost:8761" -ForegroundColor Cyan
Write-Host "To stop: close each window; run 'docker compose down' to stop infra." -ForegroundColor Yellow
