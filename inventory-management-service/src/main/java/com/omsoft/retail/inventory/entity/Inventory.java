package com.omsoft.retail.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Setter
@Getter
public class Inventory {

    @Id
    private Long productId;

    private Integer available;
    private Integer reserved;

    @Version
    private Long version; // optimistic locking
}
