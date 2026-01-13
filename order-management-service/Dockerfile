FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/order-management-service.jar order-management-service*.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","order-management-service*.jar"]
