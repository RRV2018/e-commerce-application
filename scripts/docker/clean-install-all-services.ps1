$ErrorActionPreference = "Stop"

function Invoke-Docker {
    param(
        [Parameter(Mandatory = $true)]
        [string[]] $DockerArguments
    )

    & docker @DockerArguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker $($DockerArguments -join ' ') failed with exit code $LASTEXITCODE"
    }
}

Write-Host "==============================="
Write-Host "Maven clean package (all modules)"
Write-Host "==============================="
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    throw "Maven clean package failed with exit code $LASTEXITCODE"
}

Write-Host ""
Write-Host "==============================="
Write-Host "Stopping existing containers..."
Write-Host "==============================="
Invoke-Docker -DockerArguments @("compose", "down")

Write-Host ""
Write-Host "==============================="
Write-Host "Building common base image..."
Write-Host "==============================="
Invoke-Docker -DockerArguments @("build", "-t", "omsoft/common-base-image:1.0", "docker/base")
Invoke-Docker -DockerArguments @("tag", "omsoft/common-base-image:1.0", "omsoft/common-base-image:latest")

Write-Host ""
Write-Host "==============================="
Write-Host "Building microservice images..."
Write-Host "==============================="

Invoke-Docker -DockerArguments @("build", "-f", "docker/api-gateway-service/Dockerfile", "-t", "api-gateway-service", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/property-config-server/Dockerfile", "-t", "property-config-server", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/user-management-service/Dockerfile", "-t", "user-management-service", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/product-catalog-service/Dockerfile", "-t", "product-catalog-service", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/inventory-management-service/Dockerfile", "-t", "inventory-management-service", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/order-management-service/Dockerfile", "-t", "order-management-service", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/payment-management-service/Dockerfile", "-t", "payment-management-service", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/eureka-discovery-server/Dockerfile", "-t", "eureka-discovery-server", ".")
Invoke-Docker -DockerArguments @("build", "-f", "docker/app-monitoring-admin-server/Dockerfile", "-t", "app-monitoring-admin-server", ".")

Write-Host "FE Application==============================="
Invoke-Docker -DockerArguments @("build", "--no-cache", "-f", "docker/e-commerce-fe/Dockerfile", "-t", "e-commerce-fe", "./e-commerce-fe")

Write-Host ""
Write-Host "==============================="
Write-Host "Starting all containers..."
Write-Host "==============================="
Invoke-Docker -DockerArguments @("compose", "up", "-d", "--pull", "missing")
