package com.omsoft.retail.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_options")
@Getter
@Setter
public class ShippingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
