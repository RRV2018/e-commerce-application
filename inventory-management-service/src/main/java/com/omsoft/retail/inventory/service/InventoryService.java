package com.omsoft.retail.inventory.service;

import com.omsoft.retail.inventory.entity.Inventory;
import com.omsoft.retail.inventory.repo.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }
    public void reserve(Long productId, int quantity) {
        Inventory inventory = repo.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));
        if (inventory.getAvailable() < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        inventory.setAvailable(inventory.getAvailable() - quantity);
        inventory.setReserved(inventory.getReserved() + quantity);
        repo.save(inventory);
    }

    public void release(Long productId, int quantity) {

        Inventory inventory = repo.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));

        inventory.setAvailable(inventory.getAvailable() + quantity);
        inventory.setReserved(inventory.getReserved() - quantity);

        repo.save(inventory);
    }

    public void confirm(Long productId, int quantity) {
        Inventory inventory = repo.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));
        inventory.setReserved(inventory.getReserved() - quantity);
        repo.save(inventory);
    }
}
