FROM eclipse-temurin:17-jdk-alpine
# Install curl
RUN apk add --no-cache curl
WORKDIR /app
COPY target/user-management-service.jar user-management-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","user-management-service.jar"]