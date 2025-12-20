package com.omsoft.retail.order.client;

import com.omsoft.retail.order.dto.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "inventory-management-service",
        path = "/api/inventory"
)
public interface InventoryClient {

    @PostMapping("/reserve")
    void reserve(@RequestBody InventoryRequest request);

    @PostMapping("/confirm")
    void confirm(@RequestBody InventoryRequest request);

    @PostMapping("/release")
    void release(@RequestBody InventoryRequest request);
}
