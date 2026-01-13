FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/property-config-server.jar property-config-server.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","property-config-server.jar"]