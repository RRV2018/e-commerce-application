package com.omsoft.retail.order.client;

import com.omsoft.retail.order.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-catalog-service",
        path = "/api/products")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Product getProduct(@PathVariable Long id);
}
