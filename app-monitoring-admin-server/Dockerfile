FROM eclipse-temurin:17-jdk-alpine
# Install curl
RUN apk add --no-cache curl

WORKDIR /app
COPY target/app-monitoring-admin-server.jar app-monitoring-admin-server.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","app-monitoring-admin-server.jar"]
