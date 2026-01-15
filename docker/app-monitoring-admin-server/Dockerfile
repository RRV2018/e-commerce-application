FROM omsoft/common-base-image:1.0

COPY app-monitoring-admin-server/target/app-monitoring-admin-server.jar app-monitoring-admin-server.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","app-monitoring-admin-server.jar"]
