FROM eclipse-temurin:17-jdk-alpine
# Install curl
RUN apk add --no-cache curl
WORKDIR /app
COPY target/inventory-management-service.jar inventory-management-service.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","inventory-management-service.jar"]
