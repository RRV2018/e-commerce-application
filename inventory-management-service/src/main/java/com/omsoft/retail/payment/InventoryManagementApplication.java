package com.omsoft.retail.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class InventoryManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
        log.info("Inventory management service is up.......");
    }
}