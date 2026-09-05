FROM omsoft/common-base-image:latest

COPY property-config-server/target/property-config-server.jar property-config-server.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","property-config-server.jar"]