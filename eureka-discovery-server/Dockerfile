FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/eureka-discovery-server.jar eureka-discovery-server.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","eureka-discovery-server.jar"]