package com.omsoft.retail.payment.repo;

import com.omsoft.retail.payment.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
