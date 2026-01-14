FROM eclipse-temurin:17-jdk-alpine
# Install curl
RUN apk add --no-cache curl

WORKDIR /app
COPY target/api-gateway-service.jar api-gateway-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","api-gateway-service.jar"]
