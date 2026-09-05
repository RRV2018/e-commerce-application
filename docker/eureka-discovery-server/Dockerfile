FROM omsoft/common-base-image:latest

COPY eureka-discovery-server/target/eureka-discovery-server.jar eureka-discovery-server.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","eureka-discovery-server.jar"]