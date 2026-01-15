FROM omsoft/common-base-image:1.0

COPY api-gateway-service/target/api-gateway-service.jar api-gateway-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","api-gateway-service.jar"]
