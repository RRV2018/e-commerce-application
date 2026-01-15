FROM omsoft/common-base-image:1.0

COPY order-management-service/target/order-management-service.jar order-management-service.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","order-management-service.jar"]
