package com.omsoft.retail.inventory.controller;

import com.omsoft.retail.inventory.dto.InventoryRequest;
import com.omsoft.retail.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/reserve")
    public void reserve(@RequestBody InventoryRequest dto) {
        service.reserve(dto.productId(), dto.quantity());
    }

    @PostMapping("/confirm")
    public void confirm(@RequestBody InventoryRequest dto) {
        service.confirm(dto.productId(), dto.quantity());
    }

    @PostMapping("/release")
    public void release(@RequestBody InventoryRequest dto) {
        service.release(dto.productId(), dto.quantity());
    }
}
