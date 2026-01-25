package com.omsoft.retail.inventory.repo;

import com.omsoft.retail.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
