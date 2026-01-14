FROM eclipse-temurin:17-jdk-alpine
# Install curl
RUN apk add --no-cache curl
WORKDIR /app
COPY target/product-catalog-service.jar product-catalog-service.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","product-catalog-service.jar"]