package com.omsoft.retail.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentManagementApplication.class, args);
        System.out.println("Payment management service is up.......");
    }
}