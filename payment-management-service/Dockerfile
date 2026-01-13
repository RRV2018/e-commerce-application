FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/payment-management-service.jar payment-management-service.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","payment-management-service.jar"]