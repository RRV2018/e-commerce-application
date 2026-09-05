FROM omsoft/common-base-image:latest

COPY inventory-management-service/target/inventory-management-service.jar inventory-management-service.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","inventory-management-service.jar"]
