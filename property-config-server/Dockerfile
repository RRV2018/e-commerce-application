FROM eclipse-temurin:17-jdk-alpine
# Install curl
RUN apk add --no-cache curl

WORKDIR /app
COPY target/property-config-server.jar property-config-server.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","property-config-server.jar"]