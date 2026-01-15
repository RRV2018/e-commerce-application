FROM omsoft/common-base-image:1.0

COPY product-catalog-service/target/product-catalog-service.jar product-catalog-service.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","product-catalog-service.jar"]