FROM omsoft/common-base-image:1.0

COPY eureka-discovery-server/target/eureka-discovery-server.jar eureka-discovery-server.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","eureka-discovery-server.jar"]