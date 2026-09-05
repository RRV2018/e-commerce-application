FROM omsoft/common-base-image:latest

COPY user-management-service/target/user-management-service.jar user-management-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","user-management-service.jar"]