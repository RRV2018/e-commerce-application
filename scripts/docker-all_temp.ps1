mvn clean package -DskipTests

docker compose down

docker build -t omsoft/common-base-image:1.0 docker/base
docker tag omsoft/common-base-image:1.0 omsoft/common-base-image:latest

docker build -f docker/api-gateway-service/Dockerfile -t api-gateway-service .
docker build -f docker/property-config-server/Dockerfile -t property-config-server .
docker build -f docker/user-management-service/Dockerfile -t user-management-service .
docker build -f docker/product-catalog-service/Dockerfile -t product-catalog-service .
docker build -f docker/inventory-management-service/Dockerfile -t inventory-management-service .
docker build -f docker/order-management-service/Dockerfile -t order-management-service .
docker build -f docker/payment-management-service/Dockerfile -t payment-management-service .
docker build -f docker/eureka-discovery-server/Dockerfile -t eureka-discovery-server .
docker build -f docker/app-monitoring-admin-server/Dockerfile -t app-monitoring-admin-server .

docker compose up --build
