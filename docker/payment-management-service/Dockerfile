FROM omsoft/common-base-image:latest

COPY payment-management-service/target/payment-management-service.jar payment-management-service.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","payment-management-service.jar"]