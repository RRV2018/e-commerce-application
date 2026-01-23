@echo off
setlocal enabledelayedexpansion


echo ===============================
echo Maven clean package (all modules)
echo ===============================
mvn clean package -DskipTests
IF ERRORLEVEL 1 GOTO :error

echo ===============================
echo Stopping existing containers...
echo ===============================
docker compose down

IF ERRORLEVEL 1 GOTO :error

echo.
echo ===============================
echo Building common base image...
echo ===============================
docker build -t omsoft/common-base-image:1.0 docker/base
IF ERRORLEVEL 1 GOTO :error

docker tag omsoft/common-base-image:1.0 omsoft/common-base-image:latest
IF ERRORLEVEL 1 GOTO :error

echo.
echo ===============================
echo Building microservice images...
echo ===============================

docker build -f docker\api-gateway-service\Dockerfile -t api-gateway-service .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\property-config-server\Dockerfile -t property-config-server .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\user-management-service\Dockerfile -t user-management-service .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\product-catalog-service\Dockerfile -t product-catalog-service .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\inventory-management-service\Dockerfile -t inventory-management-service .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\order-management-service\Dockerfile -t order-management-service .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\payment-management-service\Dockerfile -t payment-management-service .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\eureka-discovery-server\Dockerfile -t eureka-discovery-server .
IF ERRORLEVEL 1 GOTO :error

docker build -f docker\app-monitoring-admin-server\Dockerfile -t app-monitoring-admin-server .
IF ERRORLEVEL 1 GOTO :error

echo.
echo ===============================
echo Starting all containers...
echo ===============================
docker compose pull
docker compose up
IF ERRORLEVEL 1 GOTO :error

echo.
echo All services started successfully!
GOTO :eof

:error
echo.
echo ERROR: Docker command failed. Stopping script.
exit /b 1
