package com.omsoft.retail.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.omsoft.retail.order.client")
public class OrderManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderManagementApplication.class, args);
        System.out.println("Order management service is up.......");
    }
}