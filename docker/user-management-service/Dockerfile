FROM omsoft/common-base-image:1.0

COPY user-management-service/target/user-management-service.jar user-management-service.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","user-management-service.jar"]