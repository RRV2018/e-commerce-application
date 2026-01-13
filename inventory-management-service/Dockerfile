FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/inventory-management-service.jar inventory-management-service.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","inventory-management-service.jar"]
