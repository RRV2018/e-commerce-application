package com.omsoft.retail.inventory.service;

import com.omsoft.retail.inventory.entity.Inventory;
import com.omsoft.retail.inventory.repo.InventoryRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository repo;
    private static final String NOT_FOUND = "Inventory is not found for %s product id.";

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }
    public void reserve(Long productId, int quantity) {
        Inventory inventory = repo.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, productId)));
        if (inventory.getAvailable() < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        inventory.setAvailable(inventory.getAvailable() - quantity);
        inventory.setReserved(inventory.getReserved() + quantity);
        repo.save(inventory);
    }

    public void release(Long productId, int quantity) {

        Inventory inventory = repo.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, productId)));

        inventory.setAvailable(inventory.getAvailable() + quantity);
        inventory.setReserved(inventory.getReserved() - quantity);

        repo.save(inventory);
    }

    public void confirm(Long productId, int quantity) {
        Inventory inventory = repo.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, productId)));
        inventory.setReserved(inventory.getReserved() - quantity);
        repo.save(inventory);
    }
}
