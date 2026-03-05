package com.omsoft.retail.inventory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.TimeZone;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@Slf4j
public class InventoryManagementApplication {
    public static void main(String[] args) {
        // Use Asia/Kolkata so PostgreSQL accepts it (rejects Asia/Calcutta)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(InventoryManagementApplication.class, args);
        log.info("Inventory management service is up.......");
    }
}