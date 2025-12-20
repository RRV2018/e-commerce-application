package com.omsoft.retail.payment.service;

import com.omsoft.retail.payment.entity.Inventory;
import com.omsoft.retail.payment.repo.InventoryRepository;
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
